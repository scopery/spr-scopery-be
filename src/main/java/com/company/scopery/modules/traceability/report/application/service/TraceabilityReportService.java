package com.company.scopery.modules.traceability.report.application.service;

import com.company.scopery.modules.traceability.report.application.response.TraceCoverageMatrixResponse;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TraceabilityReportService {

    @PersistenceContext
    private EntityManager em;

    private final TraceabilityAuthorizationService authorization;

    public TraceabilityReportService(TraceabilityAuthorizationService authorization) {
        this.authorization = authorization;
    }

    // CTE for latest result per requirement (used in both queries)
    private static final String LATEST_CTE = """
            WITH latest AS (
                SELECT DISTINCT ON (tl.source_id)
                    tl.source_id      AS req_id,
                    tcr.result_status AS latest_result,
                    tcr.executed_at   AS latest_result_at,
                    tcr.test_case_id  AS latest_tc_id,
                    tcr.test_run_id   AS latest_run_id
                FROM traceability_link tl
                JOIN quality_test_case_result tcr
                    ON  tcr.test_case_id = tl.target_id
                    AND tcr.project_id   = :projectId
                WHERE tl.source_type = 'REQUIREMENT'
                  AND tl.link_type   = 'TESTED_BY'
                  AND tl.status      = 'ACTIVE'
                  AND tl.project_id  = :projectId
                ORDER BY tl.source_id, tcr.executed_at DESC NULLS LAST
            ),
            per_req AS (
                SELECT
                    r.id                 AS req_id,
                    r.code               AS req_code,
                    r.title              AS req_title,
                    r.requirement_type   AS req_type,
                    r.priority,
                    r.application_id,
                    r.functional_item_id AS fi_id,
                    COUNT(DISTINCT tl.target_id)                                         AS tc_count,
                    COUNT(DISTINCT tcr.id) FILTER (WHERE tcr.result_status IS NOT NULL)  AS executed_count,
                    COUNT(DISTINCT tcr.id) FILTER (WHERE tcr.result_status = 'PASSED')   AS passed_count,
                    COUNT(DISTINCT tcr.id) FILTER (WHERE tcr.result_status = 'FAILED')   AS failed_count,
                    COUNT(DISTINCT tcr.id) FILTER (WHERE tcr.result_status = 'BLOCKED')  AS blocked_count,
                    COUNT(DISTINCT tcr.id) FILTER (WHERE tcr.result_status IN ('NOT_RUN','SKIPPED')) AS not_run_count,
                    COUNT(DISTINCT d.id)   FILTER (WHERE d.status IN ('OPEN','TRIAGED','ASSIGNED','IN_PROGRESS','REOPENED')) AS open_defect_count,
                    l.latest_result,
                    l.latest_result_at,
                    l.latest_tc_id,
                    l.latest_run_id,
                    ltc.code  AS latest_tc_code,
                    ltc.title AS latest_tc_title
                FROM requirements_requirement r
                LEFT JOIN traceability_link tl
                    ON  tl.source_id   = r.id
                    AND tl.source_type = 'REQUIREMENT'
                    AND tl.link_type   = 'TESTED_BY'
                    AND tl.status      = 'ACTIVE'
                    AND tl.project_id  = r.project_id
                LEFT JOIN quality_test_case_result tcr
                    ON  tcr.test_case_id = tl.target_id
                    AND tcr.project_id   = r.project_id
                LEFT JOIN quality_defect d ON d.source_test_case_result_id = tcr.id
                LEFT JOIN latest l ON l.req_id = r.id
                LEFT JOIN quality_test_case ltc ON ltc.id = l.latest_tc_id
                WHERE r.project_id = :projectId
                  AND r.status NOT IN ('ARCHIVED','REJECTED')
                GROUP BY r.id, r.code, r.title, r.requirement_type, r.priority,
                         r.application_id, r.functional_item_id,
                         l.latest_result, l.latest_result_at, l.latest_tc_id, l.latest_run_id,
                         ltc.code, ltc.title
            ),
            agg AS (
                SELECT *,
                    CASE
                        WHEN tc_count = 0 THEN 'MISSING_TESTS'
                        WHEN executed_count = 0 THEN 'NOT_EVALUATED'
                        WHEN latest_result IN ('FAILED','BLOCKED') OR open_defect_count > 0 THEN 'AT_RISK'
                        ELSE 'COVERED'
                    END AS coverage_status
                FROM per_req
            )
            """;

    @Transactional(readOnly = true)
    public TraceCoverageMatrixResponse coverageMatrix(UUID projectId, String q, String coverageStatus,
                                                      String latestResult, String priority,
                                                      int limit, int offset) {
        authorization.requireView(projectId);

        TraceCoverageMatrixResponse.Summary summary = querySummary(projectId);
        List<TraceCoverageMatrixResponse.Row> items = queryItems(projectId, q, coverageStatus, latestResult, priority, limit, offset);
        long total = items.isEmpty() ? 0 : ((Number) em.createNativeQuery(
                LATEST_CTE + "SELECT COUNT(*) FROM agg" + buildWhereClause(q, priority, coverageStatus, latestResult))
                .setParameter("projectId", projectId)
                .getSingleResult()).longValue();

        return new TraceCoverageMatrixResponse(summary, items, new TraceCoverageMatrixResponse.PageInfo(limit, offset, total));
    }

    private TraceCoverageMatrixResponse.Summary querySummary(UUID projectId) {
        String sql = LATEST_CTE + """
                SELECT
                    COUNT(*)                                                  AS requirements,
                    COUNT(*) FILTER (WHERE coverage_status = 'COVERED')      AS covered,
                    COUNT(*) FILTER (WHERE coverage_status = 'MISSING_TESTS') AS missing_tests,
                    COUNT(*) FILTER (WHERE coverage_status = 'NOT_EVALUATED') AS not_evaluated,
                    COUNT(*) FILTER (WHERE coverage_status = 'AT_RISK')      AS at_risk,
                    COUNT(*) FILTER (WHERE latest_result = 'FAILED')         AS failed,
                    COUNT(*) FILTER (WHERE latest_result = 'BLOCKED')        AS blocked
                FROM agg
                """;
        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .getSingleResult();

        long requirements = toLong(row[0]);
        long covered      = toLong(row[1]);
        long missing      = toLong(row[2]);
        long notEvaluated = toLong(row[3]);
        long atRisk       = toLong(row[4]);
        long failed       = toLong(row[5]);
        long blocked      = toLong(row[6]);

        return TraceCoverageMatrixResponse.Summary.of(requirements, covered, missing, notEvaluated, atRisk, failed, blocked);
    }

    @SuppressWarnings("unchecked")
    private List<TraceCoverageMatrixResponse.Row> queryItems(UUID projectId, String q, String coverageStatus,
                                                              String latestResult, String priority,
                                                              int limit, int offset) {
        String where = buildWhereClause(q, priority, coverageStatus, latestResult);
        String sql = LATEST_CTE +
                "SELECT req_id, req_code, req_title, req_type, priority, application_id, fi_id," +
                " tc_count, executed_count, passed_count, failed_count, blocked_count, not_run_count," +
                " open_defect_count, latest_result, latest_result_at, latest_tc_id, latest_run_id," +
                " latest_tc_code, latest_tc_title, coverage_status" +
                " FROM agg" + where +
                " ORDER BY req_code ASC LIMIT :limit OFFSET :offset";

        Query nq = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("limit", limit)
                .setParameter("offset", offset);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");
        if (priority != null) nq.setParameter("priority", priority);
        if (coverageStatus != null) nq.setParameter("coverageStatus", coverageStatus);
        if (latestResult != null) nq.setParameter("latestResult", latestResult);

        List<Object[]> rows = nq.getResultList();
        List<TraceCoverageMatrixResponse.Row> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(new TraceCoverageMatrixResponse.Row(
                    toUUID(r[0]),          // requirementId
                    str(r[1]),             // code
                    str(r[2]),             // title
                    str(r[3]),             // requirementType
                    str(r[4]),             // priority
                    toUUID(r[5]),          // applicationId
                    toUUID(r[6]),          // functionalItemId
                    str(r[20]),            // coverageStatus
                    toLong(r[7]),          // testCaseCount
                    toLong(r[8]),          // executedCount
                    toLong(r[9]),          // passedCount
                    toLong(r[10]),         // failedCount
                    toLong(r[11]),         // blockedCount
                    toLong(r[12]),         // notRunCount
                    str(r[14]),            // latestResult
                    toInstant(r[15]),      // latestResultAt
                    toUUID(r[17]),         // latestTestRunId
                    toUUID(r[16]),         // latestTestCaseId
                    str(r[18]),            // latestTestCaseCode
                    str(r[19]),            // latestTestCaseTitle
                    toLong(r[13])          // openDefectCount
            ));
        }
        return result;
    }

    private String buildWhereClause(String q, String priority, String coverageStatus, String latestResult) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (q != null && !q.isBlank())
            where.append(" AND (LOWER(req_code) LIKE :q OR LOWER(req_title) LIKE :q)");
        if (priority != null)
            where.append(" AND priority = :priority");
        if (coverageStatus != null)
            where.append(" AND coverage_status = :coverageStatus");
        if (latestResult != null)
            where.append(" AND latest_result = :latestResult");
        return where.toString();
    }

    private static long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof BigInteger bi) return bi.longValue();
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        return 0L;
    }

    private static UUID toUUID(Object v) {
        if (v == null) return null;
        if (v instanceof UUID u) return u;
        return UUID.fromString(v.toString());
    }

    private static String str(Object v) {
        return v == null ? null : v.toString();
    }

    private static Instant toInstant(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toInstant();
        if (v instanceof Instant i) return i;
        return null;
    }
}
