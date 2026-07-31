package com.company.scopery.modules.traceability.usecase.domain.model;

import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseCompletenessStatus;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseStatus;

import java.time.Instant;
import java.util.UUID;

public record UseCase(
        UUID id,
        UUID projectId,
        UUID primaryFunctionId,
        String key,
        String name,
        String goal,
        String primaryActorName,
        String triggerText,
        UseCaseStatus status,
        UseCaseCompletenessStatus completenessStatus,
        int displayOrder,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCase create(
            UUID projectId,
            UUID primaryFunctionId,
            String key,
            String name,
            String goal,
            String primaryActorName,
            String triggerText
    ) {
        return new UseCase(
                UUID.randomUUID(), projectId, primaryFunctionId,
                key, name, goal, primaryActorName, triggerText,
                UseCaseStatus.DRAFT, UseCaseCompletenessStatus.NOT_STARTED,
                0, 0, null, null);
    }

    public UseCase withUpdated(
            String name,
            String goal,
            String primaryActorName,
            String triggerText,
            UseCaseStatus status
    ) {
        return new UseCase(id, projectId, primaryFunctionId,
                key, name, goal, primaryActorName, triggerText,
                status, completenessStatus, displayOrder, version, createdAt, updatedAt);
    }
}
