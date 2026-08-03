package com.company.scopery.modules.traceability.aimapping.application.query;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record ListMappingSuggestionsQuery(
        UUID projectId,
        UUID runId,
        String relationType,
        String reviewStatus,
        String confidenceBand,
        String decision,
        Boolean hasWarning,
        UUID sourceId,
        UUID targetId,
        Pageable pageable
) {}
