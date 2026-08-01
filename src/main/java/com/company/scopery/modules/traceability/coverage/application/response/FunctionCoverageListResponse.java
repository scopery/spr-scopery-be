package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FunctionCoverageListResponse(
        List<FunctionCoverageItem> items,
        PageInfo page,
        Instant generatedAt
) {
    public record FunctionCoverageItem(
            UUID functionId,
            String code,
            String title,
            long linkedRequirementCount,
            long requirementsCoveredByUseCases,
            long useCaseCount,
            long specificationReadyCount,
            long testedUseCaseCount,
            String coverageStatus,
            String nextAction,
            List<RequirementCoverRow> requirementCovers
    ) {}

    public record RequirementCoverRow(
            UUID requirementId,
            String code,
            String title,
            boolean covered,
            List<SimpleRef> coveringUseCases
    ) {}

    public record SimpleRef(UUID id, String code, String name) {}

    public record PageInfo(int limit, int offset, long total) {}
}
