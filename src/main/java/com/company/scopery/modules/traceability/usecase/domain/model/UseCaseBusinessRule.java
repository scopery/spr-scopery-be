package com.company.scopery.modules.traceability.usecase.domain.model;

import java.time.Instant;
import java.util.UUID;

public record UseCaseBusinessRule(
        UUID id,
        UUID useCaseId,
        String ruleCode,
        String description,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseBusinessRule create(
            UUID useCaseId,
            String ruleCode,
            String description,
            int displayOrder
    ) {
        return new UseCaseBusinessRule(
                UUID.randomUUID(), useCaseId, ruleCode, description, displayOrder, null, null);
    }

    public UseCaseBusinessRule withUpdated(String ruleCode, String description, int displayOrder) {
        return new UseCaseBusinessRule(id, useCaseId, ruleCode, description, displayOrder, createdAt, updatedAt);
    }
}
