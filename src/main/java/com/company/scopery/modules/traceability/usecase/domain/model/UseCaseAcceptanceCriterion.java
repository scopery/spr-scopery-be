package com.company.scopery.modules.traceability.usecase.domain.model;

import java.time.Instant;
import java.util.UUID;

public record UseCaseAcceptanceCriterion(
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
    public static UseCaseAcceptanceCriterion create(
            UUID useCaseId,
            String title,
            String givenText,
            String whenText,
            String thenText,
            int displayOrder
    ) {
        return new UseCaseAcceptanceCriterion(
                UUID.randomUUID(), useCaseId, title,
                givenText, whenText, thenText, displayOrder, null, null);
    }

    public UseCaseAcceptanceCriterion withUpdated(
            String title,
            String givenText,
            String whenText,
            String thenText,
            int displayOrder
    ) {
        return new UseCaseAcceptanceCriterion(
                id, useCaseId, title, givenText, whenText, thenText, displayOrder, createdAt, updatedAt);
    }
}
