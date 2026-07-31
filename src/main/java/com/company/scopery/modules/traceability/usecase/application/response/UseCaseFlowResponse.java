package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlow;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UseCaseFlowResponse(
        UUID id,
        UUID useCaseId,
        String flowType,
        String name,
        UUID sourceStepId,
        String conditionText,
        int displayOrder,
        List<UseCaseFlowStepResponse> steps,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseFlowResponse from(UseCaseFlow f, List<UseCaseFlowStepResponse> steps) {
        return new UseCaseFlowResponse(
                f.id(), f.useCaseId(), f.flowType().name(), f.name(),
                f.sourceStepId(), f.conditionText(), f.displayOrder(),
                steps, f.createdAt(), f.updatedAt());
    }
}
