package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RequirementTraceDetailResponse(
        RequirementInfo requirement,
        String coverageStatus,
        List<String> gapCodes,
        CoverageScore coverageScore,
        List<TraceObject> functions,
        List<TraceObject> useCases,
        List<TraceObject> implementationObjects,
        List<TraceObject> testCases,
        List<GapItem> gaps,
        Instant generatedAt
) {
    public record RequirementInfo(
            UUID id,
            String code,
            String title,
            String requirementType,
            String priority,
            String requiresUseCase,
            boolean requiresUseCaseResolved
    ) {}

    public record CoverageScore(int pct, Layers layers) {}

    public record Layers(boolean function, boolean useCase, boolean implementation, boolean test, boolean passing) {}

    public record TraceObject(
            UUID id,
            String objectType,
            String code,
            String name,
            String relationKind,
            String completenessStatus,
            String latestResult
    ) {}

    public record GapItem(
            String gapCode,
            String priority,
            String message,
            String recommendedAction,
            TraceObject relatedObject
    ) {}
}
