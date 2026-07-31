package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record CoverageSummaryResponse(
        long requirements,
        long completeCount,
        int completeCoveragePct,
        long partialCount,
        long missingFunctions,
        long missingUseCases,
        long missingImplementation,
        long missingTests,
        long failedTests,
        long blocked,
        LayerCoverage layerCoverage,
        List<FunnelStage> funnel,
        List<ByType> byRequirementType,
        Instant generatedAt,
        boolean stale
) {
    public record LayerCoverage(int functionPct, int useCasePct, int implementationPct, int testPct) {}
    public record FunnelStage(String stage, long count) {}
    public record ByType(String requirementType, long total, long completeCount, int completePct, Map<String, Long> gapCounts) {}
}
