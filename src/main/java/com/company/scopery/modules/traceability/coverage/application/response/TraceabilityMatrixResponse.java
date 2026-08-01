package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TraceabilityMatrixResponse(
        Summary summary,
        List<MatrixItem> items,
        PageInfo page,
        Instant generatedAt,
        boolean stale
) {
    public record Summary(
            long requirements,
            long completeCount,
            int completeCoveragePct,
            long partialCount,
            long missingFunctions,
            long missingUseCases,
            long missingImplementation,
            long missingTests,
            long failedTests,
            long blocked
    ) {}

    public record MatrixItem(
            UUID requirementId,
            String code,
            String title,
            String requirementType,
            String priority,
            String requiresUseCase,
            boolean requiresUseCaseResolved,
            String coverageStatus,
            List<String> gapCodes,
            long functionCount,
            long useCaseCount,
            long implementationCount,
            long testCaseCount,
            String latestResult,
            Instant latestResultAt,
            long openDefectCount,
            Previews previews,
            PreviewMore previewMore,
            long functionsCoveredByUseCase,
            long useCasesWithTests,
            ExecutionSummary executionSummary,
            String functionLayerStatus,
            String useCaseLayerStatus,
            String testLayerStatus
    ) {}

    public record ExecutionSummary(long passed, long failed, long blocked, long notRun) {}

    public record Previews(
            List<PreviewObject> functions,
            List<PreviewObject> useCases,
            List<PreviewObject> implementation,
            List<PreviewObject> testCases
    ) {}

    public record PreviewMore(long functions, long useCases, long implementation, long testCases) {}

    public record PreviewObject(UUID id, String objectType, String code, String name, String relationKind) {}

    public record PageInfo(int limit, int offset, long total) {}
}
