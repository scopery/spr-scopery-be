package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRule;

import java.time.Instant;
import java.util.UUID;

public record UseCaseBusinessRuleResponse(
        UUID id,
        UUID useCaseId,
        String ruleCode,
        String description,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseBusinessRuleResponse from(UseCaseBusinessRule r) {
        return new UseCaseBusinessRuleResponse(
                r.id(), r.useCaseId(), r.ruleCode(),
                r.description(), r.displayOrder(), r.createdAt(), r.updatedAt());
    }
}
