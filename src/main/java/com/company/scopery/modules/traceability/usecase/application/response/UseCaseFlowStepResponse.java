package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStep;

import java.time.Instant;
import java.util.UUID;

public record UseCaseFlowStepResponse(
        UUID id,
        UUID flowId,
        String stepType,
        UUID screenContextId,
        String contentJson,
        UUID nextScreenId,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseFlowStepResponse from(UseCaseFlowStep s) {
        return new UseCaseFlowStepResponse(
                s.id(), s.flowId(), s.stepType().name(),
                s.screenContextId(), s.contentJson(),
                s.nextScreenId(), s.displayOrder(),
                s.createdAt(), s.updatedAt());
    }
}
