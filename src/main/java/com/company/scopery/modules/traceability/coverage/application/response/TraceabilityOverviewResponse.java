package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;

/** Overview strip + dual pipelines + needs-attention for Traceability home. */
public record TraceabilityOverviewResponse(
        Strip strip,
        Pipeline functionalPipeline,
        Pipeline nfrPipeline,
        List<AttentionItem> needsAttention,
        Instant generatedAt
) {
    public record Strip(
            long requirementsMapped,
            long requirementsTotal,
            long functionsMissingUseCases,
            long useCasesMissingTests,
            long implementationGaps,
            long nfrsNotVerified
    ) {}

    public record Pipeline(String kind, List<PipelineStage> stages) {}

    public record PipelineStage(String stage, long count) {}

    public record AttentionItem(
            String code,
            String message,
            String actionLabel,
            String deepLinkTab,
            String deepLinkSegment,
            String deepLinkFilter
    ) {}
}
