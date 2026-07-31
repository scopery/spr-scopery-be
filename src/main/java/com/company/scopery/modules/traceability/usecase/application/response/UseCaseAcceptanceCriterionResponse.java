package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterion;

import java.time.Instant;
import java.util.UUID;

public record UseCaseAcceptanceCriterionResponse(
        UUID id,
        UUID useCaseId,
        String title,
        String givenText,
        String whenText,
        String thenText,
        int displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseAcceptanceCriterionResponse from(UseCaseAcceptanceCriterion ac) {
        return new UseCaseAcceptanceCriterionResponse(
                ac.id(), ac.useCaseId(), ac.title(),
                ac.givenText(), ac.whenText(), ac.thenText(),
                ac.displayOrder(), ac.createdAt(), ac.updatedAt());
    }
}
