package com.company.scopery.modules.traceability.usecase.domain.model;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowStepType;

import java.time.Instant;
import java.util.UUID;

public record UseCaseFlowStep(
        UUID id,
        UUID flowId,
        UseCaseFlowStepType stepType,
        UUID screenContextId,
        String contentJson,
        UUID nextScreenId,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseFlowStep create(
            UUID flowId,
            UseCaseFlowStepType stepType,
            UUID screenContextId,
            String contentJson,
            UUID nextScreenId,
            int displayOrder
    ) {
        return new UseCaseFlowStep(
                UUID.randomUUID(), flowId, stepType,
                screenContextId, contentJson, nextScreenId, displayOrder, null, null);
    }

    public UseCaseFlowStep withUpdated(
            UseCaseFlowStepType stepType,
            UUID screenContextId,
            String contentJson,
            UUID nextScreenId
    ) {
        return new UseCaseFlowStep(id, flowId, stepType,
                screenContextId, contentJson, nextScreenId, displayOrder, createdAt, updatedAt);
    }

    public UseCaseFlowStep withDisplayOrder(int order) {
        return new UseCaseFlowStep(id, flowId, stepType,
                screenContextId, contentJson, nextScreenId, order, createdAt, updatedAt);
    }
}
