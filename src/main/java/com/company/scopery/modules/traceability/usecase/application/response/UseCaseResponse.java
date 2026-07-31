package com.company.scopery.modules.traceability.usecase.application.response;

import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;

import java.time.Instant;
import java.util.UUID;

public record UseCaseResponse(
        UUID id,
        UUID projectId,
        UUID primaryFunctionId,
        String primaryFunctionName,
        String key,
        String name,
        String goal,
        String primaryActorName,
        String triggerText,
        String status,
        String completenessStatus,
        int displayOrder,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static UseCaseResponse from(UseCase uc, String primaryFunctionName) {
        return new UseCaseResponse(
                uc.id(), uc.projectId(), uc.primaryFunctionId(), primaryFunctionName,
                uc.key(), uc.name(), uc.goal(), uc.primaryActorName(), uc.triggerText(),
                uc.status().name(), uc.completenessStatus().name(),
                uc.displayOrder(), uc.version(), uc.createdAt(), uc.updatedAt());
    }
}
