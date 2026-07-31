package com.company.scopery.modules.traceability.usecase.domain.model;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseConditionType;

import java.time.Instant;
import java.util.UUID;

public record UseCaseCondition(
        UUID id,
        UUID useCaseId,
        UseCaseConditionType conditionType,
        String content,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseCondition create(
            UUID useCaseId,
            UseCaseConditionType conditionType,
            String content,
            int displayOrder
    ) {
        return new UseCaseCondition(
                UUID.randomUUID(), useCaseId, conditionType, content, displayOrder, null, null);
    }

    public UseCaseCondition withUpdated(String content, int displayOrder) {
        return new UseCaseCondition(id, useCaseId, conditionType, content, displayOrder, createdAt, updatedAt);
    }
}
