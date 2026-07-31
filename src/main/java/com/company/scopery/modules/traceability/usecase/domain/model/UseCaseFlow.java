package com.company.scopery.modules.traceability.usecase.domain.model;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowType;

import java.time.Instant;
import java.util.UUID;

public record UseCaseFlow(
        UUID id,
        UUID useCaseId,
        UseCaseFlowType flowType,
        String name,
        UUID sourceStepId,
        String conditionText,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseFlow create(
            UUID useCaseId,
            UseCaseFlowType flowType,
            String name,
            UUID sourceStepId,
            String conditionText,
            int displayOrder
    ) {
        return new UseCaseFlow(
                UUID.randomUUID(), useCaseId, flowType,
                name, sourceStepId, conditionText, displayOrder, null, null);
    }

    public UseCaseFlow withUpdated(String name, UUID sourceStepId, String conditionText) {
        return new UseCaseFlow(id, useCaseId, flowType,
                name, sourceStepId, conditionText, displayOrder, createdAt, updatedAt);
    }
}
