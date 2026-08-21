package com.company.scopery.modules.quality.testcase.infrastructure.persistence;
import com.company.scopery.modules.quality.testcase.domain.model.*;
import com.company.scopery.modules.quality.testcase.infrastructure.mapper.TestCasePersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
public class JpaTestCaseRepository implements TestCaseRepository {
    private final SpringDataTestCaseJpaRepository springData;
    private final TestCasePersistenceMapper mapper;
    @PersistenceContext private EntityManager em;

    public JpaTestCaseRepository(SpringDataTestCaseJpaRepository springData, TestCasePersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }

    @Override public TestCase save(TestCase e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCase> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestCase> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code) { return code != null && springData.existsByProjectIdAndCode(projectId, code); }
    @Override public void delete(UUID id, UUID projectId) {
        springData.findByIdAndProjectId(id, projectId).ifPresent(springData::delete);
    }

    @Override
    public List<TestCaseListRow> searchList(UUID projectId, String q, String type, String priority,
            String status, UUID assigneeId, String automationStatus, UUID requirementId, UUID useCaseId,
            String latestResult, Boolean hasOpenDefect, String orderBy, int limit, long offset) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, q, type, priority, status, assigneeId, automationStatus,
                requirementId, useCaseId, latestResult, hasOpenDefect);
        String sql = """
                SELECT tc.id, tc.project_id, tc.code, tc.title, tc.type, tc.priority, tc.status,
                       tc.assignee_id, u.full_name AS assignee_display_name,
                       tc.automation_status, tc.version, tc.created_at, tc.updated_at,
                       COUNT(DISTINCT s.id) FILTER (WHERE s.archived_at IS NULL) AS step_count,
                       COUNT(DISTINCT cov_r.id) FILTER (WHERE cov_r.archived_at IS NULL AND cov_r.target_type = 'REQUIREMENT') AS req_count,
                       COUNT(DISTINCT cov_uc.id) FILTER (WHERE cov_uc.archived_at IS NULL AND cov_uc.target_type = 'USE_CASE') AS uc_count,
                       lr.result_status AS latest_result, lr.executed_at AS latest_result_at,
                       tc.use_case_id
                FROM quality_test_case tc
                LEFT JOIN iam_user u ON u.id = tc.assignee_id
                LEFT JOIN quality_test_case_step s ON s.test_case_id = tc.id
                LEFT JOIN quality_test_case_coverage cov_r ON cov_r.test_case_id = tc.id AND cov_r.target_type = 'REQUIREMENT'
                LEFT JOIN quality_test_case_coverage cov_uc ON cov_uc.test_case_id = tc.id AND cov_uc.target_type = 'USE_CASE'
                LEFT JOIN LATERAL (
                    SELECT result_status, executed_at FROM quality_test_case_result
                    WHERE test_case_id = tc.id AND project_id = :projectId
                    ORDER BY executed_at DESC NULLS LAST LIMIT 1
                ) lr ON TRUE
                """ + where + "\n" + """
                GROUP BY tc.id, tc.project_id, tc.code, tc.title, tc.type, tc.priority, tc.status,
                         tc.assignee_id, u.full_name, tc.automation_status, tc.version, tc.created_at, tc.updated_at,
                         lr.result_status, lr.executed_at, tc.use_case_id
                ORDER BY\s""" + orderBy + " LIMIT :limit OFFSET :offset";
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        nq.setParameter("limit", limit);
        nq.setParameter("offset", offset);
        @SuppressWarnings("unchecked") List<Object[]> rows = nq.getResultList();
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public long countSearch(UUID projectId, String q, String type, String priority,
            String status, UUID assigneeId, String automationStatus, UUID requirementId, UUID useCaseId,
            String latestResult, Boolean hasOpenDefect) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, q, type, priority, status, assigneeId, automationStatus,
                requirementId, useCaseId, latestResult, hasOpenDefect);
        String sql = "SELECT COUNT(*) FROM quality_test_case tc " + where;
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        return ((Number) nq.getSingleResult()).longValue();
    }

    private StringBuilder buildWhere(Map<String, Object> params, UUID projectId, String q, String type,
            String priority, String status, UUID assigneeId, String automationStatus,
            UUID requirementId, UUID useCaseId, String latestResult, Boolean hasOpenDefect) {
        var where = new StringBuilder("WHERE tc.project_id = :projectId");
        params.put("projectId", projectId);
        if (q != null && !q.isBlank()) { where.append(" AND (tc.title ILIKE :q OR tc.code ILIKE :q)"); params.put("q", "%" + q.trim() + "%"); }
        if (type != null && !type.isBlank()) { where.append(" AND tc.type = :type"); params.put("type", type.toUpperCase()); }
        if (priority != null && !priority.isBlank()) { where.append(" AND tc.priority = :priority"); params.put("priority", priority.toUpperCase()); }
        if (status != null && !status.isBlank()) { where.append(" AND tc.status = :status"); params.put("status", status.toUpperCase()); }
        if (assigneeId != null) { where.append(" AND tc.assignee_id = :assigneeId"); params.put("assigneeId", assigneeId); }
        if (automationStatus != null && !automationStatus.isBlank()) { where.append(" AND tc.automation_status = :automationStatus"); params.put("automationStatus", automationStatus.toUpperCase()); }
        if (requirementId != null) { where.append(" AND EXISTS (SELECT 1 FROM quality_test_case_coverage cx WHERE cx.test_case_id = tc.id AND cx.target_type = 'REQUIREMENT' AND cx.target_id = :requirementId AND cx.archived_at IS NULL)"); params.put("requirementId", requirementId); }
        if (useCaseId != null) { where.append(" AND tc.use_case_id = :useCaseId"); params.put("useCaseId", useCaseId); }
        if (latestResult != null && !latestResult.isBlank()) { where.append(" AND lr.result_status = :latestResult"); params.put("latestResult", latestResult.toUpperCase()); }
        if (Boolean.TRUE.equals(hasOpenDefect)) { where.append(" AND EXISTS (SELECT 1 FROM quality_test_case_result xr WHERE xr.test_case_id = tc.id AND xr.defect_id IS NOT NULL)"); }
        return where;
    }

    private TestCaseListRow mapRow(Object[] row) {
        int i = 0;
        UUID id = uuid(row[i++]); UUID projectId = uuid(row[i++]);
        String code = str(row[i++]); String title = str(row[i++]);
        String type = str(row[i++]); String priority = str(row[i++]); String status = str(row[i++]);
        UUID assigneeId = uuid(row[i++]); String assigneeDisplayName = str(row[i++]);
        String automationStatus = str(row[i++]);
        long version = num(row[i++]);
        Instant createdAt = instant(row[i++]); Instant updatedAt = instant(row[i++]);
        long stepCount = num(row[i++]); long reqCount = num(row[i++]); long ucCount = num(row[i++]);
        String latestResult = str(row[i++]); Instant latestResultAt = instant(row[i++]);
        UUID useCaseId = uuid(row[i]);
        return new TestCaseListRow(id, projectId, code, title, type, priority, status,
                assigneeId, assigneeDisplayName, automationStatus, version, createdAt, updatedAt,
                stepCount, reqCount, ucCount, latestResult, latestResultAt, useCaseId);
    }

    private UUID uuid(Object v) { return v == null ? null : UUID.fromString(v.toString()); }
    private String str(Object v) { return v == null ? null : v.toString(); }
    private long num(Object v) { if (v == null) return 0L; if (v instanceof Number n) return n.longValue(); return Long.parseLong(v.toString()); }
    private Instant instant(Object v) { if (v == null) return null; if (v instanceof Timestamp ts) return ts.toInstant(); return Instant.parse(v.toString()); }
}
