package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.testrun.domain.model.*;
import com.company.scopery.modules.quality.testrun.infrastructure.mapper.TestRunPersistenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
public class JpaTestRunRepository implements TestRunRepository {
    private final SpringDataTestRunJpaRepository springData;
    private final TestRunPersistenceMapper mapper;
    @PersistenceContext private EntityManager em;

    public JpaTestRunRepository(SpringDataTestRunJpaRepository springData, TestRunPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }

    @Override public TestRun save(TestRun e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestRun> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestRun> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }

    @Override
    public List<TestRunListRow> searchList(UUID projectId, String q, String status, int limit, long offset) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, q, status);
        String sql = """
                SELECT tr.id, tr.project_id, tr.name, tr.run_type, tr.run_scope, tr.status,
                       COUNT(r.id) AS total,
                       COUNT(r.id) FILTER (WHERE r.result_status <> 'NOT_RUN') AS executed,
                       COUNT(r.id) FILTER (WHERE r.result_status = 'PASSED') AS passed,
                       COUNT(r.id) FILTER (WHERE r.result_status = 'FAILED') AS failed,
                       COUNT(r.id) FILTER (WHERE r.result_status = 'BLOCKED') AS blocked,
                       COUNT(r.id) FILTER (WHERE r.result_status = 'SKIPPED') AS skipped,
                       tr.started_at, tr.completed_at, tr.created_at,
                       rp.name AS release_package_name, de.name AS deployment_environment_name
                FROM quality_test_run tr
                LEFT JOIN quality_test_case_result r ON r.test_run_id = tr.id
                LEFT JOIN quality_release_package rp ON rp.id = tr.release_package_id
                LEFT JOIN quality_deployment_environment de ON de.id = tr.deployment_environment_id
                """ + where + "\n" + """
                GROUP BY tr.id, tr.project_id, tr.name, tr.run_type, tr.run_scope, tr.status,
                         tr.started_at, tr.completed_at, tr.created_at, rp.name, de.name
                ORDER BY tr.created_at DESC
                LIMIT :limit OFFSET :offset""";
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        nq.setParameter("limit", limit);
        nq.setParameter("offset", offset);
        @SuppressWarnings("unchecked") List<Object[]> rows = nq.getResultList();
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public long countSearch(UUID projectId, String q, String status) {
        var params = new LinkedHashMap<String, Object>();
        var where = buildWhere(params, projectId, q, status);
        String sql = "SELECT COUNT(*) FROM quality_test_run tr " + where;
        var nq = em.createNativeQuery(sql);
        params.forEach(nq::setParameter);
        return ((Number) nq.getSingleResult()).longValue();
    }

    private StringBuilder buildWhere(Map<String, Object> params, UUID projectId, String q, String status) {
        var where = new StringBuilder("WHERE tr.project_id = :projectId");
        params.put("projectId", projectId);
        if (q != null && !q.isBlank()) { where.append(" AND tr.name ILIKE :q"); params.put("q", "%" + q.trim() + "%"); }
        if (status != null && !status.isBlank()) { where.append(" AND tr.status = :status"); params.put("status", status.toUpperCase()); }
        return where;
    }

    private TestRunListRow mapRow(Object[] row) {
        int i = 0;
        UUID id = uuid(row[i++]); UUID projectId = uuid(row[i++]);
        String name = str(row[i++]); String runType = str(row[i++]); String runScope = str(row[i++]); String status = str(row[i++]);
        long total = num(row[i++]); long executed = num(row[i++]);
        long passed = num(row[i++]); long failed = num(row[i++]);
        long blocked = num(row[i++]); long skipped = num(row[i++]);
        Instant startedAt = instant(row[i++]); Instant completedAt = instant(row[i++]); Instant createdAt = instant(row[i++]);
        String releasePackageName = str(row[i++]); String deploymentEnvironmentName = str(row[i]);
        return new TestRunListRow(id, projectId, name, runType, runScope, status, total, executed,
                passed, failed, blocked, skipped, startedAt, completedAt, createdAt,
                releasePackageName, deploymentEnvironmentName);
    }

    private UUID uuid(Object v) { return v == null ? null : UUID.fromString(v.toString()); }
    private String str(Object v) { return v == null ? null : v.toString(); }
    private long num(Object v) { if (v == null) return 0L; if (v instanceof Number n) return n.longValue(); return Long.parseLong(v.toString()); }
    private Instant instant(Object v) { if (v == null) return null; if (v instanceof Timestamp ts) return ts.toInstant(); return Instant.parse(v.toString()); }
}
