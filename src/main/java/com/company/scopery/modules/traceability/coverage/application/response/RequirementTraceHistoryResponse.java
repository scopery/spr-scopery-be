package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;

public record RequirementTraceHistoryResponse(
        List<HistoryEntry> items,
        PageInfo page,
        Instant generatedAt
) {
    public record HistoryEntry(
            String id,
            String action,
            String actorId,
            String actorName,
            String message,
            Instant occurredAt
    ) {}

    public record PageInfo(int limit, int offset, long total) {}
}
