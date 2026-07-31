package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.testrun.domain.model.*;
import com.company.scopery.modules.quality.testrun.infrastructure.mapper.TestCaseResultPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
public class JpaTestCaseResultRepository implements TestCaseResultRepository {
    private final SpringDataTestCaseResultJpaRepository springData;
    private final TestCaseResultPersistenceMapper mapper;
    @PersistenceContext private EntityManager em;

    public JpaTestCaseResultRepository(SpringDataTestCaseResultJpaRepository springData, TestCaseResultPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }

    @Override public TestCaseResult save(TestCaseResult e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCaseResult> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestCaseResult> findByTestRunId(UUID testRunId) { return springData.findByTestRunIdOrderByCreatedAtAsc(testRunId).stream().map(mapper::toDomain).toList(); }
    @Override public List<TestCaseResult> findByTestRunIdAndProjectId(UUID testRunId, UUID projectId) { return springData.findByTestRunIdAndProjectIdOrderByCreatedAtAsc(testRunId, projectId).stream().map(mapper::toDomain).toList(); }

    @Override
    public List<TestCaseResultRow> searchList(UUID projectId, UUID testRunId, String q, String result,
            UUID assigneeId, Boolean hasDefect, int limit, long offset) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, testRunId, q, result, assigneeId, hasDefect);
        String sql = "SELECT r.id, r.test_run_id, tc.id AS tc_id, tc.code AS tc_code, tc.title AS tc_title," +
                " r.result_status, r.defect_id, r.comment, r.assignee_id, r.executed_at, r.created_at, r.version" +
                " FROM quality_test_case_result r LEFT JOIN quality_test_case tc ON tc.id = r.test_case_id " +
                where + " ORDER BY r.created_at ASC LIMIT :limit OFFSET :offset";
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        nq.setParameter("limit", limit);
        nq.setParameter("offset", offset);
        @SuppressWarnings("unchecked") List<Object[]> rows = nq.getResultList();
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public long countSearch(UUID projectId, UUID testRunId, String q, String result,
            UUID assigneeId, Boolean hasDefect) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, testRunId, q, result, assigneeId, hasDefect);
        String sql = "SELECT COUNT(*) FROM quality_test_case_result r" +
                " LEFT JOIN quality_test_case tc ON tc.id = r.test_case_id " + where;
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        return ((Number) nq.getSingleResult()).longValue();
    }

    private StringBuilder buildWhere(Map<String, Object> params, UUID projectId, UUID testRunId,
            String q, String result, UUID assigneeId, Boolean hasDefect) {
        var where = new StringBuilder("WHERE r.test_run_id = :testRunId AND r.project_id = :projectId");
        params.put("testRunId", testRunId); params.put("projectId", projectId);
        if (result != null && !result.isBlank()) { where.append(" AND r.result_status = :result"); params.put("result", result.toUpperCase()); }
        if (assigneeId != null) { where.append(" AND r.assignee_id = :assigneeId"); params.put("assigneeId", assigneeId); }
        if (Boolean.TRUE.equals(hasDefect)) { where.append(" AND r.defect_id IS NOT NULL"); }
        if (q != null && !q.isBlank()) { where.append(" AND (tc.title ILIKE :q OR tc.code ILIKE :q)"); params.put("q", "%" + q.trim() + "%"); }
        return where;
    }

    private TestCaseResultRow mapRow(Object[] row) {
        int i = 0;
        UUID id = uuid(row[i++]); UUID testRunId = uuid(row[i++]);
        UUID tcId = uuid(row[i++]); String tcCode = str(row[i++]); String tcTitle = str(row[i++]);
        String resultStatus = str(row[i++]); UUID defectId = uuid(row[i++]);
        String comment = str(row[i++]); UUID assigneeId = uuid(row[i++]);
        Instant executedAt = instant(row[i++]); Instant createdAt = instant(row[i++]);
        long version = num(row[i]);
        return new TestCaseResultRow(id, testRunId, tcId, tcCode, tcTitle,
                resultStatus, defectId, comment, assigneeId, executedAt, createdAt, version);
    }

    private UUID uuid(Object v) { return v == null ? null : UUID.fromString(v.toString()); }
    private String str(Object v) { return v == null ? null : v.toString(); }
    private long num(Object v) { if (v == null) return 0L; if (v instanceof Number n) return n.longValue(); return Long.parseLong(v.toString()); }
    private Instant instant(Object v) { if (v == null) return null; if (v instanceof Timestamp ts) return ts.toInstant(); return Instant.parse(v.toString()); }
}
