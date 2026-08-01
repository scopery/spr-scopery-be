package com.company.scopery.modules.traceability.coverage.application.service;

import com.company.scopery.modules.traceability.coverage.application.response.*;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Overview, Function/UC pivots, Implementation, NFR Verification, Explorer.
 */
@Service
public class TraceabilityMultiViewQueryService {

    @PersistenceContext
    private EntityManager em;

    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityCoverageQueryService coverageQuery;

    public TraceabilityMultiViewQueryService(
            TraceabilityAuthorizationService authorization,
            TraceabilityCoverageQueryService coverageQuery) {
        this.authorization = authorization;
        this.coverageQuery = coverageQuery;
    }

    @Transactional(readOnly = true)
    public TraceabilityOverviewResponse getOverview(UUID projectId) {
        authorization.requireView(projectId);

        CoverageSummaryResponse summary = coverageQuery.getCoverageSummary(projectId);

        long reqTotal = summary.requirements();
        long reqMapped = reqTotal - summary.missingFunctions();
        long fnMissingUc = summary.missingUseCases();
        long ucMissingTests = summary.missingTests();
        long implGaps = summary.missingImplementation();
        long nfrsNotVerified = countNfrsNotVerified(projectId);

        TraceabilityOverviewResponse.Strip strip = new TraceabilityOverviewResponse.Strip(
                Math.max(0, reqMapped), reqTotal, fnMissingUc, ucMissingTests, implGaps, nfrsNotVerified);

        List<TraceabilityOverviewResponse.PipelineStage> functionalStages = List.of(
                new TraceabilityOverviewResponse.PipelineStage("REQUIREMENTS", reqTotal),
                new TraceabilityOverviewResponse.PipelineStage("HAS_FUNCTION",
                        summary.funnel().stream().filter(f -> "HAS_FUNCTION".equals(f.stage())).findFirst().map(CoverageSummaryResponse.FunnelStage::count).orElse(0L)),
                new TraceabilityOverviewResponse.PipelineStage("HAS_USE_CASE",
                        summary.funnel().stream().filter(f -> "HAS_USE_CASE".equals(f.stage())).findFirst().map(CoverageSummaryResponse.FunnelStage::count).orElse(0L)),
                new TraceabilityOverviewResponse.PipelineStage("HAS_TEST",
                        summary.funnel().stream().filter(f -> "HAS_TEST".equals(f.stage())).findFirst().map(CoverageSummaryResponse.FunnelStage::count).orElse(0L))
        );

        long nfrTotal = countNfrRequirements(projectId);
        long nfrSpec = countNfrWithSpec(projectId);
        long nfrVerified = Math.max(0, nfrTotal - nfrsNotVerified);
        List<TraceabilityOverviewResponse.PipelineStage> nfrStages = List.of(
                new TraceabilityOverviewResponse.PipelineStage("NFR_REQUIREMENTS", nfrTotal),
                new TraceabilityOverviewResponse.PipelineStage("HAS_SPECIFICATION", nfrSpec),
                new TraceabilityOverviewResponse.PipelineStage("VERIFIED", nfrVerified)
        );

        List<TraceabilityOverviewResponse.AttentionItem> attention = new ArrayList<>();
        if (summary.missingFunctions() > 0) {
            attention.add(new TraceabilityOverviewResponse.AttentionItem(
                    "MISSING_FUNCTION",
                    summary.missingFunctions() + " requirements are missing a Function mapping",
                    "Map Functions",
                    "functional", "requirements", "MISSING_FUNCTION"));
        }
        if (fnMissingUc > 0) {
            attention.add(new TraceabilityOverviewResponse.AttentionItem(
                    "MISSING_USE_CASE",
                    fnMissingUc + " requirements have Functions but no Use Case",
                    "Add Use Cases",
                    "functional", "functions", "MISSING_USE_CASE"));
        }
        if (ucMissingTests > 0) {
            attention.add(new TraceabilityOverviewResponse.AttentionItem(
                    "MISSING_TEST",
                    ucMissingTests + " coverage chains need Test Cases",
                    "Link Tests",
                    "functional", "use-cases", "MISSING_TEST"));
        }
        if (implGaps > 0) {
            attention.add(new TraceabilityOverviewResponse.AttentionItem(
                    "MISSING_IMPLEMENTATION",
                    implGaps + " Functions lack Screens/APIs",
                    "Review Implementation",
                    "implementation", null, "MISSING_IMPLEMENTATION"));
        }
        if (nfrsNotVerified > 0) {
            attention.add(new TraceabilityOverviewResponse.AttentionItem(
                    "NFR_NOT_VERIFIED",
                    nfrsNotVerified + " NFRs are not verified",
                    "Verify NFRs",
                    "nfr", null, "NOT_VERIFIED"));
        }

        return new TraceabilityOverviewResponse(
                strip,
                new TraceabilityOverviewResponse.Pipeline("FUNCTIONAL", functionalStages),
                new TraceabilityOverviewResponse.Pipeline("NFR", nfrStages),
                attention,
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public FunctionCoverageListResponse listFunctionCoverage(
            UUID projectId, String q, String coverageStatus, int limit, int offset) {
        authorization.requireView(projectId);
        int lim = Math.min(Math.max(limit, 1), 200);
        int off = Math.max(offset, 0);

        String sql = """
                WITH fn_base AS (
                    SELECT fi.id, fi.code, fi.title
                    FROM app_functional_item fi
                    WHERE fi.project_id = :projectId
                      AND fi.status <> 'ARCHIVED'
                      AND fi.type = 'FUNCTIONAL'
                      AND (CAST(:q AS text) IS NULL OR fi.code ILIKE '%' || CAST(:q AS text) || '%' OR fi.title ILIKE '%' || CAST(:q AS text) || '%')
                ),
                fn_req AS (
                    SELECT rf.function_id, COUNT(DISTINCT rf.requirement_id) AS linked_req
                    FROM app_requirement_function rf
                    JOIN fn_base f ON f.id = rf.function_id
                    JOIN requirements_requirement r ON r.id = rf.requirement_id
                    WHERE r.status NOT IN ('ARCHIVED','REJECTED')
                    GROUP BY rf.function_id
                ),
                fn_uc AS (
                    SELECT f.id AS function_id, COUNT(DISTINCT uc.id) AS uc_count,
                           COUNT(DISTINCT uc.id) FILTER (
                               WHERE uc.completeness_status IN ('READY_FOR_REVIEW','COMPLETE')
                                 AND uc.status <> 'DRAFT'
                           ) AS spec_ready
                    FROM fn_base f
                    LEFT JOIN app_use_case uc ON uc.primary_function_id = f.id AND uc.status <> 'ARCHIVED'
                    GROUP BY f.id
                ),
                fn_uc_tested AS (
                    SELECT f.id AS function_id, COUNT(DISTINCT uc.id) AS tested_uc
                    FROM fn_base f
                    JOIN app_use_case uc ON uc.primary_function_id = f.id AND uc.status <> 'ARCHIVED'
                    WHERE EXISTS (
                        SELECT 1 FROM quality_test_case tc
                        WHERE tc.use_case_id = uc.id AND tc.project_id = :projectId
                    ) OR EXISTS (
                        SELECT 1 FROM quality_test_case_coverage cov
                        WHERE cov.target_type = 'USE_CASE' AND cov.target_id = uc.id
                          AND cov.project_id = :projectId AND cov.archived_at IS NULL
                    )
                    GROUP BY f.id
                ),
                fn_req_covered AS (
                    SELECT rf.function_id, COUNT(DISTINCT rf.requirement_id) AS covered_req
                    FROM app_requirement_function rf
                    JOIN fn_base f ON f.id = rf.function_id
                    WHERE EXISTS (
                        SELECT 1 FROM app_use_case uc
                        WHERE uc.primary_function_id = rf.function_id
                          AND uc.status NOT IN ('DRAFT','ARCHIVED')
                          AND (
                            EXISTS (SELECT 1 FROM app_requirement_use_case ruc
                                    WHERE ruc.requirement_id = rf.requirement_id AND ruc.use_case_id = uc.id)
                            OR EXISTS (SELECT 1 FROM app_requirement_use_case ruc2
                                       WHERE ruc2.requirement_id = rf.requirement_id)
                            OR TRUE
                          )
                    )
                    AND EXISTS (
                        SELECT 1 FROM app_use_case uc2
                        WHERE uc2.primary_function_id = rf.function_id
                          AND uc2.status NOT IN ('DRAFT','ARCHIVED')
                          AND (
                            EXISTS (SELECT 1 FROM app_requirement_use_case ruc
                                    WHERE ruc.requirement_id = rf.requirement_id AND ruc.use_case_id = uc2.id)
                            OR NOT EXISTS (SELECT 1 FROM app_requirement_use_case rucx
                                           WHERE rucx.requirement_id = rf.requirement_id)
                          )
                    )
                    GROUP BY rf.function_id
                )
                SELECT f.id, f.code, f.title,
                       COALESCE(fr.linked_req, 0),
                       COALESCE(frc.covered_req, 0),
                       COALESCE(fu.uc_count, 0),
                       COALESCE(fu.spec_ready, 0),
                       COALESCE(ft.tested_uc, 0)
                FROM fn_base f
                LEFT JOIN fn_req fr ON fr.function_id = f.id
                LEFT JOIN fn_req_covered frc ON frc.function_id = f.id
                LEFT JOIN fn_uc fu ON fu.function_id = f.id
                LEFT JOIN fn_uc_tested ft ON ft.function_id = f.id
                ORDER BY f.code
                """;

        List<Object[]> all = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("q", blankToNull(q))
                .getResultList();

        List<FunctionCoverageListResponse.FunctionCoverageItem> items = new ArrayList<>();
        for (Object[] r : all) {
            UUID fnId = toUUID(r[0]);
            long linked = toLong(r[3]);
            long covered = toLong(r[4]);
            long ucCount = toLong(r[5]);
            long specReady = toLong(r[6]);
            long testedUc = toLong(r[7]);

            String status = resolveFunctionCoverageStatus(linked, covered, ucCount);
            if (coverageStatus != null && !coverageStatus.isBlank() && !coverageStatus.equals(status)) {
                continue;
            }
            String next = nextFunctionAction(status, linked, ucCount);

            List<FunctionCoverageListResponse.RequirementCoverRow> covers =
                    loadFunctionRequirementCovers(fnId);
            // Recompute covered from expand for accuracy
            covered = covers.stream().filter(FunctionCoverageListResponse.RequirementCoverRow::covered).count();
            status = resolveFunctionCoverageStatus(linked, covered, ucCount);
            if (coverageStatus != null && !coverageStatus.isBlank() && !coverageStatus.equals(status)) {
                continue;
            }
            next = nextFunctionAction(status, linked, ucCount);

            items.add(new FunctionCoverageListResponse.FunctionCoverageItem(
                    fnId, str(r[1]), str(r[2]), linked, covered, ucCount, specReady, testedUc,
                    status, next, covers
            ));
        }

        long total = items.size();
        List<FunctionCoverageListResponse.FunctionCoverageItem> page =
                items.stream().skip(off).limit(lim).toList();

        return new FunctionCoverageListResponse(
                page,
                new FunctionCoverageListResponse.PageInfo(lim, off, total),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public UseCaseCoverageListResponse listUseCaseCoverage(
            UUID projectId, String q, String coverageStatus, int limit, int offset) {
        authorization.requireView(projectId);
        int lim = Math.min(Math.max(limit, 1), 200);
        int off = Math.max(offset, 0);

        String sql = """
                SELECT uc.id, uc.key, uc.name, uc.primary_function_id, fi.code, fi.title,
                       uc.completeness_status, uc.status,
                       (SELECT COUNT(*) FROM app_requirement_use_case ruc WHERE ruc.use_case_id = uc.id) AS req_count,
                       (SELECT COUNT(*) FROM app_use_case_acceptance_criterion ac WHERE ac.use_case_id = uc.id) AS ac_count,
                       (SELECT COUNT(DISTINCT tc.id) FROM (
                           SELECT tc.id FROM quality_test_case tc
                           WHERE tc.use_case_id = uc.id AND tc.project_id = :projectId
                           UNION
                           SELECT cov.test_case_id FROM quality_test_case_coverage cov
                           WHERE cov.target_type = 'USE_CASE' AND cov.target_id = uc.id
                             AND cov.project_id = :projectId AND cov.archived_at IS NULL
                       ) tc) AS tc_count,
                       (SELECT tcr.result_status FROM quality_test_case_result tcr
                        JOIN quality_test_case tc ON tc.id = tcr.test_case_id
                        WHERE tc.use_case_id = uc.id AND tcr.project_id = :projectId
                        ORDER BY tcr.executed_at DESC NULLS LAST LIMIT 1) AS latest_result
                FROM app_use_case uc
                LEFT JOIN app_functional_item fi ON fi.id = uc.primary_function_id
                WHERE uc.project_id = :projectId
                  AND uc.status <> 'ARCHIVED'
                  AND (CAST(:q AS text) IS NULL OR uc.key ILIKE '%' || CAST(:q AS text) || '%' OR uc.name ILIKE '%' || CAST(:q AS text) || '%')
                ORDER BY uc.key
                """;

        List<Object[]> all = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("q", blankToNull(q))
                .getResultList();

        List<UseCaseCoverageListResponse.UseCaseCoverageItem> items = new ArrayList<>();
        for (Object[] r : all) {
            UUID ucId = toUUID(r[0]);
            long acCount = toLong(r[9]);
            long tcCount = toLong(r[10]);
            String completeness = str(r[6]);
            String statusEnum = str(r[7]);
            String specStatus = resolveSpecStatus(completeness, statusEnum, acCount);
            String covStatus = resolveUseCaseCoverageStatus(specStatus, tcCount);
            if (coverageStatus != null && !coverageStatus.isBlank() && !coverageStatus.equals(covStatus)) {
                continue;
            }

            List<UseCaseCoverageListResponse.AcceptanceCriterionCover> acs = loadAcceptanceCriteria(ucId);
            List<UseCaseCoverageListResponse.SimpleRef> tcs = loadUseCaseTestCases(ucId, projectId);
            String next = nextUseCaseAction(specStatus, tcCount);

            items.add(new UseCaseCoverageListResponse.UseCaseCoverageItem(
                    ucId, str(r[1]), str(r[2]), toUUID(r[3]), str(r[4]), str(r[5]),
                    toLong(r[8]), specStatus, acCount, tcCount, str(r[11]),
                    covStatus, next, acs, tcs
            ));
        }

        long total = items.size();
        List<UseCaseCoverageListResponse.UseCaseCoverageItem> page =
                items.stream().skip(off).limit(lim).toList();

        return new UseCaseCoverageListResponse(
                page,
                new UseCaseCoverageListResponse.PageInfo(lim, off, total),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public ImplementationCoverageListResponse listImplementationCoverage(
            UUID projectId, String q, String coverageStatus, int limit, int offset) {
        authorization.requireView(projectId);
        int lim = Math.min(Math.max(limit, 1), 200);
        int off = Math.max(offset, 0);

        String sql = """
                SELECT fi.id, fi.code, fi.title,
                       (SELECT COUNT(*) FROM app_function_screen fs WHERE fs.function_id = fi.id) AS screens,
                       (SELECT COUNT(*) FROM app_function_api fa WHERE fa.function_id = fi.id) AS apis,
                       (SELECT COUNT(*) FROM app_screen_component sc
                        JOIN app_function_screen fs2 ON fs2.screen_id = sc.screen_id
                        WHERE fs2.function_id = fi.id) AS components,
                       0 AS entities,
                       0 AS tasks
                FROM app_functional_item fi
                WHERE fi.project_id = :projectId
                  AND fi.status <> 'ARCHIVED'
                  AND fi.type = 'FUNCTIONAL'
                  AND (CAST(:q AS text) IS NULL OR fi.code ILIKE '%' || CAST(:q AS text) || '%' OR fi.title ILIKE '%' || CAST(:q AS text) || '%')
                ORDER BY fi.code
                """;

        List<Object[]> all = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("q", blankToNull(q))
                .getResultList();

        List<ImplementationCoverageListResponse.ImplementationCoverageItem> items = new ArrayList<>();
        for (Object[] r : all) {
            UUID fnId = toUUID(r[0]);
            long screens = toLong(r[3]);
            long apis = toLong(r[4]);
            long components = toLong(r[5]);
            long entities = toLong(r[6]);
            long tasks = toLong(r[7]);
            boolean hasAny = screens + apis > 0;
            String status = hasAny ? (screens > 0 && apis > 0 ? "COMPLETE" : "PARTIAL") : "MISSING";
            if (coverageStatus != null && !coverageStatus.isBlank() && !coverageStatus.equals(status)) {
                continue;
            }
            String next = !hasAny ? "Link Screen or API" : (screens == 0 ? "Link Screen" : apis == 0 ? "Link API" : "Review");

            items.add(new ImplementationCoverageListResponse.ImplementationCoverageItem(
                    fnId, str(r[1]), str(r[2]), screens, apis, components, entities, tasks,
                    status, next,
                    loadImplRefs(fnId, "screen"),
                    loadImplRefs(fnId, "api")
            ));
        }

        long total = items.size();
        List<ImplementationCoverageListResponse.ImplementationCoverageItem> page =
                items.stream().skip(off).limit(lim).toList();

        return new ImplementationCoverageListResponse(
                page,
                new ImplementationCoverageListResponse.PageInfo(lim, off, total),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public NfrVerificationListResponse listNfrVerification(
            UUID projectId, String q, String coverageStatus, int limit, int offset) {
        authorization.requireView(projectId);
        int lim = Math.min(Math.max(limit, 1), 200);
        int off = Math.max(offset, 0);

        String sql = """
                SELECT r.id, r.code, r.title,
                       nfr.quality_attribute,
                       (SELECT COUNT(*) FROM quality_nfr_target t WHERE t.requirement_id = r.id) AS target_count,
                       (SELECT COUNT(*) FROM quality_verification_case vc
                        WHERE vc.requirement_id = r.id AND vc.project_id = :projectId
                          AND vc.archived_at IS NULL) AS vc_count,
                       nfr.metric_name,
                       (SELECT vcr.result_status FROM quality_verification_case_result vcr
                        JOIN quality_verification_case vc ON vc.id = vcr.verification_case_id
                        WHERE vc.requirement_id = r.id AND vcr.project_id = :projectId
                        ORDER BY vcr.executed_at DESC NULLS LAST LIMIT 1) AS latest_result
                FROM requirements_requirement r
                LEFT JOIN quality_nfr_specification nfr ON nfr.requirement_id = r.id
                WHERE r.project_id = :projectId
                  AND r.requirement_type = 'NON_FUNCTIONAL'
                  AND r.status NOT IN ('ARCHIVED','REJECTED')
                  AND (CAST(:q AS text) IS NULL OR r.code ILIKE '%' || CAST(:q AS text) || '%' OR r.title ILIKE '%' || CAST(:q AS text) || '%')
                ORDER BY r.code
                """;

        List<Object[]> all = em.createNativeQuery(sql)
                .setParameter("projectId", projectId)
                .setParameter("q", blankToNull(q))
                .getResultList();

        List<NfrVerificationListResponse.NfrVerificationItem> items = new ArrayList<>();
        for (Object[] r : all) {
            long targets = toLong(r[4]);
            long vcs = toLong(r[5]);
            String latest = str(r[7]);
            String attr = str(r[3]);
            String status;
            if (attr == null) status = "MISSING";
            else if (vcs == 0) status = "PARTIAL";
            else if ("FAILED".equals(latest)) status = "FAILED";
            else if ("PASSED".equals(latest)) status = "COMPLETE";
            else status = "PARTIAL";

            if (coverageStatus != null && !coverageStatus.isBlank() && !coverageStatus.equals(status)) {
                continue;
            }

            String next = attr == null ? "Add NFR specification"
                    : targets == 0 ? "Add verification target"
                    : vcs == 0 ? "Add verification case"
                    : "Review results";

            items.add(new NfrVerificationListResponse.NfrVerificationItem(
                    toUUID(r[0]), str(r[1]), str(r[2]), attr, targets, vcs,
                    str(r[6]), latest, status, next
            ));
        }

        long total = items.size();
        List<NfrVerificationListResponse.NfrVerificationItem> page =
                items.stream().skip(off).limit(lim).toList();

        return new NfrVerificationListResponse(
                page,
                new NfrVerificationListResponse.PageInfo(lim, off, total),
                Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public TraceExplorerResponse getExplorer(UUID projectId, String rootType, UUID rootId) {
        authorization.requireView(projectId);
        if (rootType == null || rootId == null) {
            throw TraceabilityExceptions.functionalItemNotFound(rootId);
        }
        String type = rootType.toUpperCase(Locale.ROOT);
        TraceExplorerResponse.TraceNode root = switch (type) {
            case "REQUIREMENT" -> buildRequirementExplorer(projectId, rootId);
            case "FUNCTION" -> buildFunctionExplorer(projectId, rootId);
            case "USE_CASE" -> buildUseCaseExplorer(projectId, rootId);
            case "TEST_CASE" -> buildTestCaseExplorer(projectId, rootId);
            default -> throw TraceabilityExceptions.functionalItemNotFound(rootId);
        };
        return new TraceExplorerResponse(type, rootId, root, Instant.now());
    }

    // ---- helpers ----

    @SuppressWarnings("unchecked")
    private TraceExplorerResponse.TraceNode buildRequirementExplorer(UUID projectId, UUID reqId) {
        Object[] req = (Object[]) em.createNativeQuery(
                        "SELECT id, code, title FROM requirements_requirement WHERE id = :id AND project_id = :projectId")
                .setParameter("id", reqId).setParameter("projectId", projectId).getSingleResult();
        RequirementTraceDetailResponse detail = coverageQuery.getRequirementDetail(projectId, reqId);
        List<TraceExplorerResponse.TraceNode> fnChildren = new ArrayList<>();
        if (detail.coverageChain() != null) {
            for (var fnNode : detail.coverageChain()) {
                List<TraceExplorerResponse.TraceNode> ucChildren = new ArrayList<>();
                for (var ucNode : fnNode.useCases()) {
                    List<TraceExplorerResponse.TraceNode> tcChildren = ucNode.testCases().stream()
                            .map(tc -> new TraceExplorerResponse.TraceNode(
                                    tc.id(), "TEST_CASE", tc.code(), tc.name(), null, tc.latestResult(), List.of()))
                            .collect(Collectors.toList());
                    var uc = ucNode.useCase();
                    ucChildren.add(new TraceExplorerResponse.TraceNode(
                            uc.id(), "USE_CASE", uc.code(), uc.name(), null, null, tcChildren));
                }
                var fn = fnNode.function();
                fnChildren.add(new TraceExplorerResponse.TraceNode(
                        fn.id(), "FUNCTION", fn.code(), fn.name(), null, null, ucChildren));
            }
        }
        return new TraceExplorerResponse.TraceNode(
                toUUID(req[0]), "REQUIREMENT", str(req[1]), str(req[2]),
                detail.coverageStatus(), null, fnChildren);
    }

    @SuppressWarnings("unchecked")
    private TraceExplorerResponse.TraceNode buildFunctionExplorer(UUID projectId, UUID fnId) {
        Object[] fn = (Object[]) em.createNativeQuery(
                        "SELECT id, code, title FROM app_functional_item WHERE id = :id AND project_id = :projectId")
                .setParameter("id", fnId).setParameter("projectId", projectId).getSingleResult();
        List<Object[]> ucs = em.createNativeQuery("""
                        SELECT id, key, name FROM app_use_case
                        WHERE primary_function_id = :fnId AND project_id = :projectId AND status <> 'ARCHIVED'
                        ORDER BY key
                        """)
                .setParameter("fnId", fnId).setParameter("projectId", projectId).getResultList();
        List<TraceExplorerResponse.TraceNode> children = new ArrayList<>();
        for (Object[] u : ucs) {
            children.add(buildUseCaseExplorer(projectId, toUUID(u[0])));
        }
        return new TraceExplorerResponse.TraceNode(
                toUUID(fn[0]), "FUNCTION", str(fn[1]), str(fn[2]), null, null, children);
    }

    @SuppressWarnings("unchecked")
    private TraceExplorerResponse.TraceNode buildUseCaseExplorer(UUID projectId, UUID ucId) {
        Object[] uc = (Object[]) em.createNativeQuery(
                        "SELECT id, key, name FROM app_use_case WHERE id = :id AND project_id = :projectId")
                .setParameter("id", ucId).setParameter("projectId", projectId).getSingleResult();
        List<UseCaseCoverageListResponse.SimpleRef> tcs = loadUseCaseTestCases(ucId, projectId);
        List<TraceExplorerResponse.TraceNode> children = tcs.stream()
                .map(tc -> new TraceExplorerResponse.TraceNode(
                        tc.id(), "TEST_CASE", tc.code(), tc.name(), null, null, List.of()))
                .collect(Collectors.toList());
        return new TraceExplorerResponse.TraceNode(
                toUUID(uc[0]), "USE_CASE", str(uc[1]), str(uc[2]), null, null, children);
    }

    @SuppressWarnings("unchecked")
    private TraceExplorerResponse.TraceNode buildTestCaseExplorer(UUID projectId, UUID tcId) {
        Object[] tc = (Object[]) em.createNativeQuery(
                        "SELECT id, code, title FROM quality_test_case WHERE id = :id AND project_id = :projectId")
                .setParameter("id", tcId).setParameter("projectId", projectId).getSingleResult();
        return new TraceExplorerResponse.TraceNode(
                toUUID(tc[0]), "TEST_CASE", str(tc[1]), str(tc[2]), null, null, List.of());
    }

    @SuppressWarnings("unchecked")
    private List<FunctionCoverageListResponse.RequirementCoverRow> loadFunctionRequirementCovers(UUID fnId) {
        List<Object[]> reqs = em.createNativeQuery("""
                        SELECT r.id, r.code, r.title
                        FROM app_requirement_function rf
                        JOIN requirements_requirement r ON r.id = rf.requirement_id
                        WHERE rf.function_id = :fnId
                          AND r.status NOT IN ('ARCHIVED','REJECTED')
                        ORDER BY r.code
                        """)
                .setParameter("fnId", fnId).getResultList();

        List<FunctionCoverageListResponse.RequirementCoverRow> out = new ArrayList<>();
        for (Object[] r : reqs) {
            UUID reqId = toUUID(r[0]);
            List<Object[]> ucRows = em.createNativeQuery("""
                            SELECT DISTINCT uc.id, uc.key, uc.name
                            FROM app_use_case uc
                            WHERE uc.primary_function_id = :fnId
                              AND uc.status NOT IN ('DRAFT','ARCHIVED')
                              AND (
                                EXISTS (SELECT 1 FROM app_requirement_use_case ruc
                                        WHERE ruc.requirement_id = :reqId AND ruc.use_case_id = uc.id)
                                OR NOT EXISTS (SELECT 1 FROM app_requirement_use_case ruc2
                                               WHERE ruc2.requirement_id = :reqId)
                              )
                            ORDER BY uc.key
                            """)
                    .setParameter("fnId", fnId).setParameter("reqId", reqId).getResultList();
            List<FunctionCoverageListResponse.SimpleRef> ucs = ucRows.stream()
                    .map(u -> new FunctionCoverageListResponse.SimpleRef(toUUID(u[0]), str(u[1]), str(u[2])))
                    .toList();
            out.add(new FunctionCoverageListResponse.RequirementCoverRow(
                    reqId, str(r[1]), str(r[2]), !ucs.isEmpty(), ucs));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private List<UseCaseCoverageListResponse.AcceptanceCriterionCover> loadAcceptanceCriteria(UUID ucId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT ac.id, ac.given_text, ac.when_text, ac.then_text,
                               EXISTS (
                                   SELECT 1 FROM quality_test_case_acceptance_criterion link
                                   WHERE link.acceptance_criterion_id = ac.id
                               ) AS has_tc
                        FROM app_use_case_acceptance_criterion ac
                        WHERE ac.use_case_id = :ucId
                        ORDER BY ac.display_order, ac.created_at
                        """)
                .setParameter("ucId", ucId)
                .getResultList();
        return rows.stream()
                .map(r -> new UseCaseCoverageListResponse.AcceptanceCriterionCover(
                        toUUID(r[0]), str(r[1]), str(r[2]), str(r[3]), toBoolean(r[4])))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<UseCaseCoverageListResponse.SimpleRef> loadUseCaseTestCases(UUID ucId, UUID projectId) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT tc.id, tc.code, tc.title FROM (
                            SELECT tc.id, tc.code, tc.title FROM quality_test_case tc
                            WHERE tc.use_case_id = :ucId AND tc.project_id = :projectId
                            UNION
                            SELECT tc.id, tc.code, tc.title
                            FROM quality_test_case_coverage cov
                            JOIN quality_test_case tc ON tc.id = cov.test_case_id
                            WHERE cov.target_type = 'USE_CASE' AND cov.target_id = :ucId
                              AND cov.project_id = :projectId AND cov.archived_at IS NULL
                        ) tc ORDER BY tc.code
                        """)
                .setParameter("ucId", ucId).setParameter("projectId", projectId).getResultList();
        return rows.stream()
                .map(r -> new UseCaseCoverageListResponse.SimpleRef(toUUID(r[0]), str(r[1]), str(r[2])))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<ImplementationCoverageListResponse.SimpleRef> loadImplRefs(UUID fnId, String kind) {
        if ("screen".equals(kind)) {
            List<Object[]> rows = em.createNativeQuery("""
                            SELECT s.id, s.code, s.name FROM app_function_screen fs
                            JOIN app_registry_screen s ON s.id = fs.screen_id
                            WHERE fs.function_id = :fnId ORDER BY s.code LIMIT 10
                            """).setParameter("fnId", fnId).getResultList();
            return rows.stream()
                    .map(r -> new ImplementationCoverageListResponse.SimpleRef(toUUID(r[0]), str(r[1]), str(r[2])))
                    .toList();
        }
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT a.id, CONCAT(a.method, ' ', a.path_pattern), a.name
                        FROM app_function_api fa
                        JOIN app_registry_api_endpoint a ON a.id = fa.api_endpoint_id
                        WHERE fa.function_id = :fnId ORDER BY a.path_pattern LIMIT 10
                        """).setParameter("fnId", fnId).getResultList();
        return rows.stream()
                .map(r -> new ImplementationCoverageListResponse.SimpleRef(toUUID(r[0]), str(r[1]), str(r[2])))
                .toList();
    }

    private long countNfrRequirements(UUID projectId) {
        return toLong(em.createNativeQuery("""
                        SELECT COUNT(*) FROM requirements_requirement
                        WHERE project_id = :projectId AND requirement_type = 'NON_FUNCTIONAL'
                          AND status NOT IN ('ARCHIVED','REJECTED')
                        """).setParameter("projectId", projectId).getSingleResult());
    }

    private long countNfrWithSpec(UUID projectId) {
        return toLong(em.createNativeQuery("""
                        SELECT COUNT(*) FROM requirements_requirement r
                        JOIN quality_nfr_specification nfr ON nfr.requirement_id = r.id
                        WHERE r.project_id = :projectId AND r.requirement_type = 'NON_FUNCTIONAL'
                          AND r.status NOT IN ('ARCHIVED','REJECTED')
                        """).setParameter("projectId", projectId).getSingleResult());
    }

    private long countNfrsNotVerified(UUID projectId) {
        return toLong(em.createNativeQuery("""
                        SELECT COUNT(*) FROM requirements_requirement r
                        WHERE r.project_id = :projectId AND r.requirement_type = 'NON_FUNCTIONAL'
                          AND r.status NOT IN ('ARCHIVED','REJECTED')
                          AND NOT EXISTS (
                              SELECT 1 FROM quality_verification_case_result vcr
                              JOIN quality_verification_case vc ON vc.id = vcr.verification_case_id
                              WHERE vc.requirement_id = r.id AND vcr.result_status = 'PASSED'
                          )
                        """).setParameter("projectId", projectId).getSingleResult());
    }

    private static String resolveFunctionCoverageStatus(long linked, long covered, long ucCount) {
        if (linked == 0) return "NOT_MAPPED";
        if (ucCount == 0) return "PARTIAL";
        if (covered >= linked) return "COMPLETE";
        if (covered > 0) return "PARTIAL";
        return "PARTIAL";
    }

    private static String nextFunctionAction(String status, long linked, long ucCount) {
        if (linked == 0) return "Link Requirements";
        if (ucCount == 0) return "Create Use Case";
        if ("COMPLETE".equals(status)) return "Review";
        return "Cover remaining Requirements";
    }

    private static String resolveSpecStatus(String completeness, String status, long acCount) {
        if ("DEPRECATED".equals(status) || "ARCHIVED".equals(status)) return "DEPRECATED";
        if ("READY_FOR_REVIEW".equals(completeness) || "COMPLETE".equals(completeness) || acCount > 0) {
            return "TEST_READY";
        }
        return "INCOMPLETE";
    }

    private static String resolveUseCaseCoverageStatus(String specStatus, long tcCount) {
        if ("DEPRECATED".equals(specStatus)) return "NOT_APPLICABLE";
        if ("INCOMPLETE".equals(specStatus)) return "PARTIAL";
        if (tcCount == 0) return "PARTIAL";
        return "COMPLETE";
    }

    private static String nextUseCaseAction(String specStatus, long tcCount) {
        if ("INCOMPLETE".equals(specStatus)) return "Complete specification";
        if (tcCount == 0) return "Link Test Case";
        return "Review execution";
    }

    private static String blankToNull(String q) {
        return q == null || q.isBlank() ? null : q.trim();
    }

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
}
