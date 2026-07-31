package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseCondition;

import java.time.Instant;
import java.util.UUID;

public record UseCaseConditionResponse(
        UUID id,
        UUID useCaseId,
        String conditionType,
        String content,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseConditionResponse from(UseCaseCondition c) {
        return new UseCaseConditionResponse(
                c.id(), c.useCaseId(), c.conditionType().name(),
                c.content(), c.displayOrder(), c.createdAt(), c.updatedAt());
    }
}
