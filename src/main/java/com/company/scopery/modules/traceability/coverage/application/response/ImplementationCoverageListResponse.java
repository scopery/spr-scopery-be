package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ImplementationCoverageListResponse(
        List<ImplementationCoverageItem> items,
        PageInfo page,
        Instant generatedAt
) {
    public record ImplementationCoverageItem(
            UUID functionId,
            String code,
            String title,
            long screenCount,
            long apiCount,
            long componentCount,
            long entityCount,
            long taskCount,
            String coverageStatus,
            String nextAction,
            List<SimpleRef> screens,
            List<SimpleRef> apis
    ) {}

    public record SimpleRef(UUID id, String code, String name) {}

    public record PageInfo(int limit, int offset, long total) {}
}
