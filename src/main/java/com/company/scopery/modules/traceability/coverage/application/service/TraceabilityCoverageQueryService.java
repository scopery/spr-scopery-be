package com.company.scopery.modules.traceability.coverage.application.service;

import com.company.scopery.modules.traceability.coverage.application.response.*;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceabilityCoverageQueryService {

    @PersistenceContext
    private EntityManager em;

    private final TraceabilityAuthorizationService authorization;

    public TraceabilityCoverageQueryService(TraceabilityAuthorizationService authorization) {
        this.authorization = authorization;
    }

    // -------------------------------------------------------------------------
    // Core CTE — reused across all queries
    // -------------------------------------------------------------------------
    private static final String COVERAGE_CTE = """
            WITH
            fn_agg AS (
                SELECT requirement_id, COUNT(*) AS cnt
                FROM app_requirement_function
                GROUP BY requirement_id
            ),
            uc_agg AS (
                SELECT ruc.requirement_id,
                       COUNT(*) AS uc_count,
                       COUNT(*) FILTER (WHERE uc.completeness_status IN ('READY_FOR_REVIEW','COMPLETE')) AS complete_uc_count
                FROM app_requirement_use_case ruc
                JOIN app_use_case uc ON uc.id = ruc.use_case_id
                GROUP BY ruc.requirement_id
            ),
            impl_agg AS (
                SELECT DISTINCT rf.requirement_id
                FROM app_requirement_function rf
                WHERE EXISTS (SELECT 1 FROM app_function_screen fs WHERE fs.function_id = rf.function_id)
                   OR EXISTS (SELECT 1 FROM app_function_api fa WHERE fa.function_id = rf.function_id)
            ),
            tc_agg AS (
                SELECT tl.source_id AS requirement_id, COUNT(DISTINCT tl.target_id) AS tc_count
                FROM traceability_link tl
                WHERE tl.source_type = 'REQUIREMENT'
                  AND tl.link_type = 'TESTED_BY'
                  AND tl.status = 'ACTIVE'
                  AND tl.project_id = :projectId
                GROUP BY tl.source_id
            ),
            latest_res AS (
                SELECT DISTINCT ON (tl.source_id)
                    tl.source_id AS requirement_id,
                    tcr.result_status AS result,
                    tcr.executed_at AS result_at
                FROM traceability_link tl
                JOIN quality_test_case_result tcr ON tcr.test_case_id = tl.target_id AND tcr.project_id = :projectId
                WHERE tl.source_type = 'REQUIREMENT'
                  AND tl.link_type = 'TESTED_BY'
                  AND tl.status = 'ACTIVE'
                  AND tl.project_id = :projectId
                ORDER BY tl.source_id, tcr.executed_at DESC NULLS LAST
            ),
            cov AS (
                SELECT
                    r.id AS req_id,
                    r.code,
                    r.title,
                    r.requirement_type,
                    r.priority,
                    r.application_id,
                    COALESCE(r.requires_use_case, 'AUTO') AS requires_use_case,
                    CASE WHEN COALESCE(r.requires_use_case,'AUTO') = 'YES' THEN TRUE
                         WHEN COALESCE(r.requires_use_case,'AUTO') = 'NO'  THEN FALSE
                         WHEN r.requirement_type = 'FUNCTIONAL' THEN TRUE ELSE FALSE END AS requires_uc,
                    COALESCE(fn.cnt, 0)               AS fn_count,
                    COALESCE(uc.uc_count, 0)          AS uc_count,
                    COALESCE(uc.complete_uc_count, 0) AS cuc_count,
                    CASE WHEN impl.requirement_id IS NOT NULL THEN TRUE ELSE FALSE END AS has_impl,
                    COALESCE(tc.tc_count, 0)          AS tc_count,
                    lr.result                         AS latest_result,
                    lr.result_at                      AS latest_result_at
                FROM requirements_requirement r
                LEFT JOIN fn_agg fn     ON fn.requirement_id = r.id
                LEFT JOIN uc_agg uc     ON uc.requirement_id = r.id
                LEFT JOIN impl_agg impl ON impl.requirement_id = r.id
                LEFT JOIN tc_agg tc     ON tc.requirement_id = r.id
                LEFT JOIN latest_res lr ON lr.requirement_id = r.id
                WHERE r.project_id = :projectId
                  AND r.status NOT IN ('ARCHIVED','REJECTED')
            )
            """;

    // -------------------------------------------------------------------------
    // 1. Coverage Summary
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public CoverageSummaryResponse getCoverageSummary(UUID projectId) {
        authorization.requireView(projectId);

        String sql = COVERAGE_CTE + """
                SELECT
                    COUNT(*)                                                                    AS requirements,
                    COUNT(*) FILTER (WHERE fn_count > 0)                                       AS has_fn,
                    COUNT(*) FILTER (WHERE uc_count > 0 OR NOT requires_uc)                    AS has_uc,
                    COUNT(*) FILTER (WHERE has_impl)                                            AS has_impl,
                    COUNT(*) FILTER (WHERE tc_count > 0)                                       AS has_tc,
                    COUNT(*) FILTER (WHERE requires_uc AND fn_count = 0)                       AS missing_fn,
                    COUNT(*) FILTER (WHERE requires_uc AND uc_count = 0)                       AS missing_uc,
                    COUNT(*) FILTER (WHERE NOT has_impl)                                       AS missing_impl,
                    COUNT(*) FILTER (WHERE tc_count = 0)                                       AS missing_tc,
                    COUNT(*) FILTER (WHERE latest_result = 'FAILED')                           AS failed_tc,
                    COUNT(*) FILTER (WHERE latest_result = 'BLOCKED')                          AS blocked,
                    requirement_type
                FROM cov
                GROUP BY ROLLUP(requirement_type)
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .getResultList();

        long requirements = 0, hasFn = 0, hasUc = 0, hasImpl = 0, hasTc = 0;
        long missingFn = 0, missingUc = 0, missingImpl = 0, missingTc = 0, failedTc = 0, blocked = 0;
        List<CoverageSummaryResponse.ByType> byType = new ArrayList<>();

        for (Object[] r : rows) {
            String reqType = str(r[11]);
            if (reqType == null) {
                // ROLLUP total row
                requirements = toLong(r[0]);
                hasFn = toLong(r[1]);
                hasUc = toLong(r[2]);
                hasImpl = toLong(r[3]);
                hasTc = toLong(r[4]);
                missingFn = toLong(r[5]);
                missingUc = toLong(r[6]);
                missingImpl = toLong(r[7]);
                missingTc = toLong(r[8]);
                failedTc = toLong(r[9]);
                blocked = toLong(r[10]);
            } else {
                long total = toLong(r[0]);
                long complete = computeCompleteCountFromRow(r);
                int pct = total == 0 ? 0 : (int) Math.round(complete * 100.0 / total);
                Map<String, Long> gapCounts = new LinkedHashMap<>();
                gapCounts.put("MISSING_FUNCTION", toLong(r[5]));
                gapCounts.put("MISSING_USE_CASE", toLong(r[6]));
                gapCounts.put("MISSING_IMPLEMENTATION", toLong(r[7]));
                gapCounts.put("MISSING_TEST", toLong(r[8]));
                gapCounts.put("TEST_FAILED", toLong(r[9]));
                gapCounts.put("BLOCKED", toLong(r[10]));
                byType.add(new CoverageSummaryResponse.ByType(reqType, total, complete, pct, gapCounts));
            }
        }

        // Complete = no gaps (fn OK, uc OK, impl OK, tc OK, result not FAILED/BLOCKED)
        long completeCount = computeCompleteCountDirect(projectId);
        long partialCount = requirements - completeCount;
        int completePct = requirements == 0 ? 0 : (int) Math.round(completeCount * 100.0 / requirements);

        int fnPct = requirements == 0 ? 0 : (int) Math.round(hasFn * 100.0 / requirements);
        int ucPct = requirements == 0 ? 0 : (int) Math.round(hasUc * 100.0 / requirements);
        int implPct = requirements == 0 ? 0 : (int) Math.round(hasImpl * 100.0 / requirements);
        int tcPct = requirements == 0 ? 0 : (int) Math.round(hasTc * 100.0 / requirements);

        CoverageSummaryResponse.LayerCoverage layerCoverage =
                new CoverageSummaryResponse.LayerCoverage(fnPct, ucPct, implPct, tcPct);

        List<CoverageSummaryResponse.FunnelStage> funnel = List.of(
                new CoverageSummaryResponse.FunnelStage("REQUIREMENTS", requirements),
                new CoverageSummaryResponse.FunnelStage("HAS_FUNCTION", hasFn),
                new CoverageSummaryResponse.FunnelStage("HAS_USE_CASE", hasUc),
                new CoverageSummaryResponse.FunnelStage("HAS_IMPLEMENTATION", hasImpl),
                new CoverageSummaryResponse.FunnelStage("HAS_TEST", hasTc)
        );

        return new CoverageSummaryResponse(
                requirements, completeCount, completePct, partialCount,
                missingFn, missingUc, missingImpl, missingTc, failedTc, blocked,
                layerCoverage, funnel, byType, Instant.now(), false
        );
    }

    private long computeCompleteCountDirect(UUID projectId) {
        String sql = COVERAGE_CTE + """
                SELECT COUNT(*) FROM cov
                WHERE (NOT requires_uc OR (fn_count > 0 AND uc_count > 0 AND cuc_count > 0))
                  AND has_impl
                  AND tc_count > 0
                  AND (latest_result IS NULL OR latest_result NOT IN ('FAILED','BLOCKED'))
                """;
        Object result = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .getSingleResult();
        return toLong(result);
    }

    /** Helper: for a summary row, compute complete count (zero-gap rows). Not exact from rollup — used for byType only. */
    private long computeCompleteCountFromRow(Object[] r) {
        // A row is complete if all gap counts = 0
        long missingFn = toLong(r[5]);
        long missingUc = toLong(r[6]);
        long missingImpl = toLong(r[7]);
        long missingTc = toLong(r[8]);
        long failed = toLong(r[9]);
        long blocked2 = toLong(r[10]);
        long total = toLong(r[0]);
        long withGaps = Math.max(missingFn, Math.max(missingUc, Math.max(missingImpl, Math.max(missingTc, Math.max(failed, blocked2)))));
        // This is an approximation — gaps can overlap. Return total - withGaps as lower bound.
        return Math.max(0, total - withGaps);
    }

    // -------------------------------------------------------------------------
    // 2. Traceability Matrix
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public TraceabilityMatrixResponse getMatrix(UUID projectId, String q, String coverageStatus,
                                                String gapCode, String requirementType,
                                                boolean showGapsOnly, int limit, int offset) {
        authorization.requireView(projectId);

        // Build WHERE clause
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (q != null && !q.isBlank())
            where.append(" AND (LOWER(code) LIKE :q OR LOWER(title) LIKE :q)");
        if (requirementType != null)
            where.append(" AND requirement_type = :requirementType");

        String baseSql = COVERAGE_CTE + "SELECT req_id, code, title, requirement_type, priority, requires_use_case," +
                " requires_uc, fn_count, uc_count, cuc_count, has_impl, tc_count, latest_result, latest_result_at" +
                " FROM cov" + where + " ORDER BY code ASC";

        Query nq = em.createNativeQuery(baseSql).setParameter("projectId", projectId);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");
        if (requirementType != null) nq.setParameter("requirementType", requirementType);

        @SuppressWarnings("unchecked")
        List<Object[]> allRows = nq.getResultList();

        // Compute gap codes and coverage status in Java, then apply remaining filters
        List<RawRow> processed = new ArrayList<>();
        for (Object[] r : allRows) {
            long fnCount = toLong(r[7]);
            long ucCount = toLong(r[8]);
            long cucCount = toLong(r[9]);
            boolean hasImpl = toBoolean(r[10]);
            long tcCount = toLong(r[11]);
            String latestResult = str(r[12]);
            boolean requiresUc = toBoolean(r[6]);
            String reqType = str(r[3]);

            List<String> gaps = computeGapCodes(fnCount, ucCount, cucCount, hasImpl, tcCount, latestResult, requiresUc, reqType);
            String covStatus = gaps.isEmpty() ? "COMPLETE" : "PARTIAL";

            if (coverageStatus != null && !coverageStatus.equals(covStatus)) continue;
            if (gapCode != null && !gaps.contains(gapCode)) continue;
            if (showGapsOnly && gaps.isEmpty()) continue;

            processed.add(new RawRow(
                    toUUID(r[0]), str(r[1]), str(r[2]), reqType, str(r[4]),
                    str(r[5]), requiresUc, covStatus, gaps,
                    fnCount, ucCount, hasImpl ? 1L : 0L, tcCount,
                    latestResult, toInstant(r[13])
            ));
        }

        long total = processed.size();
        List<RawRow> page = processed.stream().skip(offset).limit(limit).toList();

        // Batch-load previews for page requirement IDs
        List<UUID> pageIds = page.stream().map(RawRow::reqId).toList();
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> fnPreviews = loadFunctionPreviews(pageIds);
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> ucPreviews = loadUseCasePreviews(pageIds);
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> implPreviews = loadImplPreviews(pageIds);
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> tcPreviews = loadTestCasePreviews(pageIds, projectId);

        List<TraceabilityMatrixResponse.MatrixItem> items = new ArrayList<>();
        for (RawRow row : page) {
            UUID rid = row.reqId();
            List<TraceabilityMatrixResponse.PreviewObject> fns = fnPreviews.getOrDefault(rid, List.of());
            List<TraceabilityMatrixResponse.PreviewObject> ucs = ucPreviews.getOrDefault(rid, List.of());
            List<TraceabilityMatrixResponse.PreviewObject> impls = implPreviews.getOrDefault(rid, List.of());
            List<TraceabilityMatrixResponse.PreviewObject> tcs = tcPreviews.getOrDefault(rid, List.of());

            TraceabilityMatrixResponse.Previews previews = new TraceabilityMatrixResponse.Previews(
                    fns.stream().limit(2).toList(),
                    ucs.stream().limit(2).toList(),
                    impls.stream().limit(2).toList(),
                    tcs.stream().limit(2).toList()
            );
            TraceabilityMatrixResponse.PreviewMore more = new TraceabilityMatrixResponse.PreviewMore(
                    Math.max(0, row.fnCount() - 2),
                    Math.max(0, row.ucCount() - 2),
                    Math.max(0, row.implCount() - 2),
                    Math.max(0, row.tcCount() - 2)
            );

            boolean requiresUcResolved = "YES".equals(row.requiresUseCase()) ||
                    ("AUTO".equals(row.requiresUseCase()) && "FUNCTIONAL".equals(row.requirementType()));

            items.add(new TraceabilityMatrixResponse.MatrixItem(
                    rid, row.code(), row.title(), row.requirementType(), row.priority(),
                    row.requiresUseCase(), requiresUcResolved,
                    row.coverageStatus(), row.gapCodes(),
                    row.fnCount(), row.ucCount(), row.implCount(), row.tcCount(),
                    row.latestResult(), row.latestResultAt(), 0L,
                    previews, more
            ));
        }

        // Summary from all processed rows (not page)
        TraceabilityMatrixResponse.Summary summary = buildMatrixSummary(processed);

        return new TraceabilityMatrixResponse(
                summary, items,
                new TraceabilityMatrixResponse.PageInfo(limit, offset, total),
                Instant.now(), false
        );
    }

    private TraceabilityMatrixResponse.Summary buildMatrixSummary(List<RawRow> rows) {
        long requirements = rows.size();
        long completeCount = rows.stream().filter(r -> r.gapCodes().isEmpty()).count();
        long partialCount = requirements - completeCount;
        int completePct = requirements == 0 ? 0 : (int) Math.round(completeCount * 100.0 / requirements);
        long missingFunctions = rows.stream().filter(r -> r.gapCodes().contains("MISSING_FUNCTION")).count();
        long missingUseCases = rows.stream().filter(r -> r.gapCodes().contains("MISSING_USE_CASE")).count();
        long missingImpl = rows.stream().filter(r -> r.gapCodes().contains("MISSING_IMPLEMENTATION")).count();
        long missingTests = rows.stream().filter(r -> r.gapCodes().contains("MISSING_TEST")).count();
        long failedTests = rows.stream().filter(r -> r.gapCodes().contains("TEST_FAILED")).count();
        long blocked = rows.stream().filter(r -> r.gapCodes().contains("BLOCKED")).count();
        return new TraceabilityMatrixResponse.Summary(
                requirements, completeCount, completePct, partialCount,
                missingFunctions, missingUseCases, missingImpl, missingTests, failedTests, blocked
        );
    }

    // -------------------------------------------------------------------------
    // 3. Requirement Detail
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public RequirementTraceDetailResponse getRequirementDetail(UUID projectId, UUID requirementId) {
        authorization.requireView(projectId);

        // Load requirement row
        String reqSql = """
                SELECT r.id, r.code, r.title, r.requirement_type, r.priority,
                       COALESCE(r.requires_use_case, 'AUTO') AS requires_use_case,
                       CASE WHEN COALESCE(r.requires_use_case,'AUTO') = 'YES' THEN TRUE
                            WHEN COALESCE(r.requires_use_case,'AUTO') = 'NO'  THEN FALSE
                            WHEN r.requirement_type = 'FUNCTIONAL' THEN TRUE ELSE FALSE END AS requires_uc
                FROM requirements_requirement r
                WHERE r.id = :reqId AND r.project_id = :projectId
                """;
        @SuppressWarnings("unchecked")
        List<Object[]> reqRows = em.createNativeQuery(reqSql)
                .setParameter("reqId", requirementId)
                .setParameter("projectId", projectId)
                .getResultList();

        if (reqRows.isEmpty()) {
            throw TraceabilityExceptions.requirementNotFound(requirementId);
        }
        Object[] rr = reqRows.get(0);
        String requiresUseCase = str(rr[5]);
        boolean requiresUc = toBoolean(rr[6]);
        boolean requiresUcResolved = "YES".equals(requiresUseCase) ||
                ("AUTO".equals(requiresUseCase) && "FUNCTIONAL".equals(str(rr[3])));

        RequirementTraceDetailResponse.RequirementInfo reqInfo = new RequirementTraceDetailResponse.RequirementInfo(
                toUUID(rr[0]), str(rr[1]), str(rr[2]), str(rr[3]), str(rr[4]),
                requiresUseCase, requiresUcResolved
        );

        // Load functions
        List<RequirementTraceDetailResponse.TraceObject> functions = loadDetailFunctions(requirementId);
        // Load use cases
        List<RequirementTraceDetailResponse.TraceObject> useCases = loadDetailUseCases(requirementId);
        // Load implementation objects (screens + api endpoints via functions)
        List<RequirementTraceDetailResponse.TraceObject> implObjects = loadDetailImplObjects(requirementId);
        // Load test cases
        List<RequirementTraceDetailResponse.TraceObject> testCases = loadDetailTestCases(requirementId, projectId);

        long fnCount = functions.size();
        long ucCount = useCases.size();
        long cucCount = useCases.stream().filter(u ->
                "READY_FOR_REVIEW".equals(u.completenessStatus()) || "COMPLETE".equals(u.completenessStatus())).count();
        boolean hasImpl = !implObjects.isEmpty();
        long tcCount = testCases.size();
        String latestResult = testCases.stream()
                .filter(t -> t.latestResult() != null)
                .map(RequirementTraceDetailResponse.TraceObject::latestResult)
                .findFirst().orElse(null);

        List<String> gapCodes = computeGapCodes(fnCount, ucCount, cucCount, hasImpl, tcCount, latestResult, requiresUc, str(rr[3]));
        String coverageStatus = gapCodes.isEmpty() ? "COMPLETE" : "PARTIAL";

        boolean fnOk = !requiresUc || fnCount > 0;
        boolean ucOk = !requiresUc || ucCount > 0;
        boolean implOk = hasImpl;
        boolean tcOk = tcCount > 0;
        boolean passingOk = !"FAILED".equals(latestResult) && !"BLOCKED".equals(latestResult);

        int scoreLayers = (fnOk ? 1 : 0) + (ucOk ? 1 : 0) + (implOk ? 1 : 0) + (tcOk ? 1 : 0) + (passingOk && tcOk ? 1 : 0);
        int totalLayers = requiresUc ? 5 : 3; // fn+uc+impl+tc+passing if requiresUc, else impl+tc+passing
        int scorePct = totalLayers == 0 ? 0 : (int) Math.round(scoreLayers * 100.0 / totalLayers);

        RequirementTraceDetailResponse.Layers layers = new RequirementTraceDetailResponse.Layers(fnOk, ucOk, implOk, tcOk, passingOk && tcOk);
        RequirementTraceDetailResponse.CoverageScore coverageScore = new RequirementTraceDetailResponse.CoverageScore(scorePct, layers);

        List<RequirementTraceDetailResponse.GapItem> gapItems = buildGapItems(gapCodes, reqInfo);

        return new RequirementTraceDetailResponse(
                reqInfo, coverageStatus, gapCodes, coverageScore,
                functions, useCases, implObjects, testCases,
                gapItems, Instant.now()
        );
    }

    // -------------------------------------------------------------------------
    // 4. Gaps
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public GapsResponse getGaps(UUID projectId, String gapCode, String priority, String requirementId,
                                String q, int limit, int offset) {
        authorization.requireView(projectId);

        // Load all requirements with coverage data
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (q != null && !q.isBlank())
            where.append(" AND (LOWER(code) LIKE :q OR LOWER(title) LIKE :q)");
        if (priority != null)
            where.append(" AND priority = :priority");
        if (requirementId != null)
            where.append(" AND req_id = :requirementId");

        String sql = COVERAGE_CTE + "SELECT req_id, code, title, priority, requirement_type," +
                " requires_use_case, requires_uc, fn_count, uc_count, cuc_count, has_impl, tc_count, latest_result" +
                " FROM cov" + where + " ORDER BY code ASC";

        Query nq = em.createNativeQuery(sql).setParameter("projectId", projectId);
        if (q != null && !q.isBlank()) nq.setParameter("q", "%" + q.trim().toLowerCase() + "%");
        if (priority != null) nq.setParameter("priority", priority);
        if (requirementId != null) nq.setParameter("requirementId", UUID.fromString(requirementId));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = nq.getResultList();

        // Expand into gap items
        List<GapsResponse.GapItem> allGaps = new ArrayList<>();
        Map<String, Long> byGapCode = new LinkedHashMap<>();

        for (Object[] r : rows) {
            UUID rid = toUUID(r[0]);
            String code = str(r[1]);
            String title = str(r[2]);
            String pri = str(r[3]);
            String reqType = str(r[4]);
            boolean requiresUc = toBoolean(r[6]);
            long fnCount = toLong(r[7]);
            long ucCount = toLong(r[8]);
            long cucCount = toLong(r[9]);
            boolean hasImpl = toBoolean(r[10]);
            long tcCount = toLong(r[11]);
            String latestResult = str(r[12]);

            List<String> gaps = computeGapCodes(fnCount, ucCount, cucCount, hasImpl, tcCount, latestResult, requiresUc, reqType);
            GapsResponse.RequirementRef ref = new GapsResponse.RequirementRef(rid, code, title);

            for (String gc : gaps) {
                if (gapCode != null && !gapCode.equals(gc)) continue;
                byGapCode.merge(gc, 1L, Long::sum);
                String id = rid + "_" + gc;
                allGaps.add(new GapsResponse.GapItem(
                        id, gc, pri, ref, null,
                        gapRecommendedAction(gc), gapMessage(gc, code)
                ));
            }
        }

        long total = allGaps.size();
        List<GapsResponse.GapItem> pageItems = allGaps.stream().skip(offset).limit(limit).toList();

        return new GapsResponse(
                new GapsResponse.GapSummary(total, byGapCode),
                pageItems,
                new GapsResponse.PageInfo(limit, offset, total),
                Instant.now(), false
        );
    }

    // -------------------------------------------------------------------------
    // 5. Requirement History
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public RequirementTraceHistoryResponse getRequirementHistory(UUID projectId, UUID requirementId,
                                                                  int limit, int offset) {
        authorization.requireView(projectId);

        // Verify requirement belongs to project
        String checkSql = "SELECT COUNT(*) FROM requirements_requirement WHERE id = :reqId AND project_id = :projectId";
        long count = toLong(em.createNativeQuery(checkSql)
                .setParameter("reqId", requirementId)
                .setParameter("projectId", projectId)
                .getSingleResult());
        if (count == 0) throw TraceabilityExceptions.requirementNotFound(requirementId);

        String countSql = """
                SELECT COUNT(*) FROM app_activity_log
                WHERE entity_type = :entityType AND entity_id = :entityId
                """;
        long total = toLong(em.createNativeQuery(countSql)
                .setParameter("entityType", TraceabilityEntityTypes.REQUIREMENT)
                .setParameter("entityId", requirementId.toString())
                .getSingleResult());

        String sql = """
                SELECT id, action, actor_id, actor_name, message, created_at
                FROM app_activity_log
                WHERE entity_type = :entityType AND entity_id = :entityId
                ORDER BY created_at DESC
                LIMIT :limit OFFSET :offset
                """;
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("entityType", TraceabilityEntityTypes.REQUIREMENT)
                .setParameter("entityId", requirementId.toString())
                .setParameter("limit", limit <= 0 ? 20 : limit)
                .setParameter("offset", offset < 0 ? 0 : offset)
                .getResultList();

        List<RequirementTraceHistoryResponse.HistoryEntry> items = rows.stream()
                .map(r -> new RequirementTraceHistoryResponse.HistoryEntry(
                        str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), toInstant(r[5])
                )).toList();

        return new RequirementTraceHistoryResponse(
                items,
                new RequirementTraceHistoryResponse.PageInfo(limit, offset, total),
                Instant.now()
        );
    }

    // -------------------------------------------------------------------------
    // Gap computation helpers
    // -------------------------------------------------------------------------
    private List<String> computeGapCodes(long fnCount, long ucCount, long cucCount,
                                          boolean hasImpl, long tcCount, String latestResult,
                                          boolean requiresUc, String requirementType) {
        List<String> gaps = new ArrayList<>();
        if (requiresUc && fnCount == 0) gaps.add("MISSING_FUNCTION");
        if (requiresUc && ucCount == 0) gaps.add("MISSING_USE_CASE");
        if (requiresUc && ucCount > 0 && cucCount == 0) gaps.add("INCOMPLETE_USE_CASE");
        if (!hasImpl) gaps.add("MISSING_IMPLEMENTATION");
        if (tcCount == 0) gaps.add("MISSING_TEST");
        if ("FAILED".equals(latestResult)) gaps.add("TEST_FAILED");
        if ("BLOCKED".equals(latestResult)) gaps.add("BLOCKED");
        return gaps;
    }

    private String gapRecommendedAction(String gapCode) {
        return switch (gapCode) {
            case "MISSING_FUNCTION" -> "Link at least one function to this requirement";
            case "MISSING_USE_CASE" -> "Link at least one use case to this requirement";
            case "INCOMPLETE_USE_CASE" -> "Mark at least one linked use case as READY_FOR_REVIEW or COMPLETE";
            case "MISSING_IMPLEMENTATION" -> "Link at least one screen or API endpoint to a function of this requirement";
            case "MISSING_TEST" -> "Link at least one test case via TESTED_BY";
            case "TEST_FAILED" -> "Investigate and fix failing test cases";
            case "BLOCKED" -> "Unblock blocked test cases";
            default -> "Review and address the gap";
        };
    }

    private String gapMessage(String gapCode, String reqCode) {
        return switch (gapCode) {
            case "MISSING_FUNCTION" -> "Requirement " + reqCode + " has no linked functions";
            case "MISSING_USE_CASE" -> "Requirement " + reqCode + " has no linked use cases";
            case "INCOMPLETE_USE_CASE" -> "Requirement " + reqCode + " has use cases but none are ready or complete";
            case "MISSING_IMPLEMENTATION" -> "Requirement " + reqCode + " has no implementation objects (screens/APIs)";
            case "MISSING_TEST" -> "Requirement " + reqCode + " has no linked test cases";
            case "TEST_FAILED" -> "Latest test result for requirement " + reqCode + " is FAILED";
            case "BLOCKED" -> "Latest test result for requirement " + reqCode + " is BLOCKED";
            default -> "Gap detected for requirement " + reqCode;
        };
    }

    private List<RequirementTraceDetailResponse.GapItem> buildGapItems(List<String> gapCodes,
                                                                         RequirementTraceDetailResponse.RequirementInfo req) {
        return gapCodes.stream().map(gc -> new RequirementTraceDetailResponse.GapItem(
                gc, priorityForGap(gc), gapMessage(gc, req.code()), gapRecommendedAction(gc), null
        )).collect(Collectors.toList());
    }

    private String priorityForGap(String gapCode) {
        return switch (gapCode) {
            case "TEST_FAILED", "BLOCKED" -> "HIGH";
            case "MISSING_TEST", "MISSING_IMPLEMENTATION" -> "MEDIUM";
            default -> "LOW";
        };
    }

    // -------------------------------------------------------------------------
    // Preview loaders
    // -------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> loadFunctionPreviews(List<UUID> reqIds) {
        if (reqIds.isEmpty()) return Map.of();
        String sql = """
                SELECT rf.requirement_id, fi.id, fi.code, fi.title
                FROM app_requirement_function rf
                JOIN app_functional_item fi ON fi.id = rf.function_id
                WHERE rf.requirement_id = ANY(:reqIds)
                ORDER BY rf.requirement_id, fi.code
                """;
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("reqIds", reqIds.toArray(new UUID[0]))
                .getResultList();
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            UUID reqId = toUUID(r[0]);
            map.computeIfAbsent(reqId, k -> new ArrayList<>())
                    .add(new TraceabilityMatrixResponse.PreviewObject(toUUID(r[1]), "FUNCTION", str(r[2]), str(r[3]), "SATISFIED_BY"));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> loadUseCasePreviews(List<UUID> reqIds) {
        if (reqIds.isEmpty()) return Map.of();
        String sql = """
                SELECT ruc.requirement_id, uc.id, uc.key, uc.name, uc.completeness_status
                FROM app_requirement_use_case ruc
                JOIN app_use_case uc ON uc.id = ruc.use_case_id
                WHERE ruc.requirement_id = ANY(:reqIds)
                ORDER BY ruc.requirement_id, uc.key
                """;
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("reqIds", reqIds.toArray(new UUID[0]))
                .getResultList();
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            UUID reqId = toUUID(r[0]);
            map.computeIfAbsent(reqId, k -> new ArrayList<>())
                    .add(new TraceabilityMatrixResponse.PreviewObject(toUUID(r[1]), "USE_CASE", str(r[2]), str(r[3]), "COVERED_BY"));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> loadImplPreviews(List<UUID> reqIds) {
        if (reqIds.isEmpty()) return Map.of();
        // Screens via functions
        String screenSql = """
                SELECT rf.requirement_id, s.id, s.code, s.name, 'SCREEN' AS obj_type
                FROM app_requirement_function rf
                JOIN app_function_screen fs ON fs.function_id = rf.function_id
                JOIN app_registry_screen s ON s.id = fs.screen_id
                WHERE rf.requirement_id = ANY(:reqIds)
                ORDER BY rf.requirement_id, s.code
                """;
        // APIs via functions
        String apiSql = """
                SELECT rf.requirement_id, ae.id, CONCAT(ae.method, ' ', ae.path_pattern) AS code, ae.name, 'API_ENDPOINT' AS obj_type
                FROM app_requirement_function rf
                JOIN app_function_api fa ON fa.function_id = rf.function_id
                JOIN app_registry_api_endpoint ae ON ae.id = fa.api_endpoint_id
                WHERE rf.requirement_id = ANY(:reqIds)
                ORDER BY rf.requirement_id, ae.path_pattern
                """;

        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> map = new LinkedHashMap<>();
        UUID[] idArr = reqIds.toArray(new UUID[0]);

        List<Object[]> screenRows = em.createNativeQuery(screenSql).setParameter("reqIds", idArr).getResultList();
        for (Object[] r : screenRows) {
            UUID reqId = toUUID(r[0]);
            map.computeIfAbsent(reqId, k -> new ArrayList<>())
                    .add(new TraceabilityMatrixResponse.PreviewObject(toUUID(r[1]), "SCREEN", str(r[2]), str(r[3]), "IMPLEMENTED_BY"));
        }
        List<Object[]> apiRows = em.createNativeQuery(apiSql).setParameter("reqIds", idArr).getResultList();
        for (Object[] r : apiRows) {
            UUID reqId = toUUID(r[0]);
            map.computeIfAbsent(reqId, k -> new ArrayList<>())
                    .add(new TraceabilityMatrixResponse.PreviewObject(toUUID(r[1]), "API_ENDPOINT", str(r[2]), str(r[3]), "IMPLEMENTED_BY"));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> loadTestCasePreviews(List<UUID> reqIds, UUID projectId) {
        if (reqIds.isEmpty()) return Map.of();
        String sql = """
                SELECT tl.source_id, tc.id, tc.code, tc.title
                FROM traceability_link tl
                JOIN quality_test_case tc ON tc.id = tl.target_id
                WHERE tl.source_type = 'REQUIREMENT'
                  AND tl.link_type = 'TESTED_BY'
                  AND tl.status = 'ACTIVE'
                  AND tl.project_id = :projectId
                  AND tl.source_id = ANY(:reqIds)
                ORDER BY tl.source_id, tc.code
                """;
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("reqIds", reqIds.toArray(new UUID[0]))
                .getResultList();
        Map<UUID, List<TraceabilityMatrixResponse.PreviewObject>> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            UUID reqId = toUUID(r[0]);
            map.computeIfAbsent(reqId, k -> new ArrayList<>())
                    .add(new TraceabilityMatrixResponse.PreviewObject(toUUID(r[1]), "TEST_CASE", str(r[2]), str(r[3]), "TESTED_BY"));
        }
        return map;
    }

    // -------------------------------------------------------------------------
    // Detail loaders (single requirement)
    // -------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private List<RequirementTraceDetailResponse.TraceObject> loadDetailFunctions(UUID requirementId) {
        String sql = """
                SELECT fi.id, fi.code, fi.title, fi.status
                FROM app_requirement_function rf
                JOIN app_functional_item fi ON fi.id = rf.function_id
                WHERE rf.requirement_id = :reqId
                ORDER BY fi.code
                """;
        List<Object[]> rows = em.createNativeQuery(sql).setParameter("reqId", requirementId).getResultList();
        return rows.stream().map(r -> new RequirementTraceDetailResponse.TraceObject(
                toUUID(r[0]), "FUNCTION", str(r[1]), str(r[2]), "SATISFIED_BY", null, null
        )).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<RequirementTraceDetailResponse.TraceObject> loadDetailUseCases(UUID requirementId) {
        String sql = """
                SELECT uc.id, uc.key, uc.name, uc.completeness_status
                FROM app_requirement_use_case ruc
                JOIN app_use_case uc ON uc.id = ruc.use_case_id
                WHERE ruc.requirement_id = :reqId
                ORDER BY uc.key
                """;
        List<Object[]> rows = em.createNativeQuery(sql).setParameter("reqId", requirementId).getResultList();
        return rows.stream().map(r -> new RequirementTraceDetailResponse.TraceObject(
                toUUID(r[0]), "USE_CASE", str(r[1]), str(r[2]), "COVERED_BY", str(r[3]), null
        )).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<RequirementTraceDetailResponse.TraceObject> loadDetailImplObjects(UUID requirementId) {
        List<RequirementTraceDetailResponse.TraceObject> result = new ArrayList<>();

        String screenSql = """
                SELECT s.id, s.code, s.name
                FROM app_requirement_function rf
                JOIN app_function_screen fs ON fs.function_id = rf.function_id
                JOIN app_registry_screen s ON s.id = fs.screen_id
                WHERE rf.requirement_id = :reqId
                ORDER BY s.code
                """;
        List<Object[]> screenRows = em.createNativeQuery(screenSql).setParameter("reqId", requirementId).getResultList();
        for (Object[] r : screenRows) {
            result.add(new RequirementTraceDetailResponse.TraceObject(
                    toUUID(r[0]), "SCREEN", str(r[1]), str(r[2]), "IMPLEMENTED_BY", null, null
            ));
        }

        String apiSql = """
                SELECT ae.id, CONCAT(ae.method, ' ', ae.path_pattern) AS code, ae.name
                FROM app_requirement_function rf
                JOIN app_function_api fa ON fa.function_id = rf.function_id
                JOIN app_registry_api_endpoint ae ON ae.id = fa.api_endpoint_id
                WHERE rf.requirement_id = :reqId
                ORDER BY ae.path_pattern
                """;
        List<Object[]> apiRows = em.createNativeQuery(apiSql).setParameter("reqId", requirementId).getResultList();
        for (Object[] r : apiRows) {
            result.add(new RequirementTraceDetailResponse.TraceObject(
                    toUUID(r[0]), "API_ENDPOINT", str(r[1]), str(r[2]), "IMPLEMENTED_BY", null, null
            ));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<RequirementTraceDetailResponse.TraceObject> loadDetailTestCases(UUID requirementId, UUID projectId) {
        String sql = """
                SELECT tc.id, tc.code, tc.title,
                       (SELECT tcr.result_status FROM quality_test_case_result tcr
                        WHERE tcr.test_case_id = tc.id AND tcr.project_id = :projectId
                        ORDER BY tcr.executed_at DESC NULLS LAST LIMIT 1) AS latest_result
                FROM traceability_link tl
                JOIN quality_test_case tc ON tc.id = tl.target_id
                WHERE tl.source_id = :reqId
                  AND tl.source_type = 'REQUIREMENT'
                  AND tl.link_type = 'TESTED_BY'
                  AND tl.status = 'ACTIVE'
                  AND tl.project_id = :projectId
                ORDER BY tc.code
                """;
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("reqId", requirementId)
                .setParameter("projectId", projectId)
                .getResultList();
        return rows.stream().map(r -> new RequirementTraceDetailResponse.TraceObject(
                toUUID(r[0]), "TEST_CASE", str(r[1]), str(r[2]), "TESTED_BY", null, str(r[3])
        )).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Internal row carrier for matrix
    // -------------------------------------------------------------------------
    private record RawRow(UUID reqId, String code, String title, String requirementType, String priority,
                          String requiresUseCase, boolean requiresUc, String coverageStatus, List<String> gapCodes,
                          long fnCount, long ucCount, long implCount, long tcCount,
                          String latestResult, Instant latestResultAt) {}

    // -------------------------------------------------------------------------
    // Type conversion helpers (same pattern as TraceabilityReportService)
    // -------------------------------------------------------------------------
    private static long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof BigInteger bi) return bi.longValue();
        if (v instanceof Long l) return l;
        if (v instanceof Number n) return n.longValue();
        return 0L;
    }

    private static boolean toBoolean(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        if (v instanceof Number n) return n.intValue() != 0;
        return Boolean.parseBoolean(v.toString());
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
