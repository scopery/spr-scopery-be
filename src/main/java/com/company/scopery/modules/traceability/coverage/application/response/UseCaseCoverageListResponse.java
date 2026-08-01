package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UseCaseCoverageListResponse(
        List<UseCaseCoverageItem> items,
        PageInfo page,
        Instant generatedAt
) {
    public record UseCaseCoverageItem(
            UUID useCaseId,
            String key,
            String name,
            UUID parentFunctionId,
            String parentFunctionCode,
            String parentFunctionName,
            long coveredRequirementCount,
            String specificationStatus,
            long acceptanceCriteriaCount,
            long testCaseCount,
            String latestResult,
            String coverageStatus,
            String nextAction,
            List<AcceptanceCriterionCover> acceptanceCriteria,
            List<SimpleRef> testCases
    ) {}

    public record AcceptanceCriterionCover(
            UUID id,
            String givenText,
            String whenText,
            String thenText,
            boolean hasTestCase
    ) {}

    public record SimpleRef(UUID id, String code, String name) {}

    public record PageInfo(int limit, int offset, long total) {}
}
