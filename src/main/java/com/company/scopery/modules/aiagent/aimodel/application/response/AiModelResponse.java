package com.company.scopery.modules.aiagent.aimodel.application.response;

import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;

import java.time.Instant;
import java.util.UUID;

public record AiModelResponse(
        UUID id,
        UUID providerId,
        String name,
        String code,
        String providerModelId,
        String type,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static AiModelResponse from(AiModel model) {
        return new AiModelResponse(
                model.id(),
                model.providerId(),
                model.name(),
                model.code().value(),
                model.providerModelId(),
                model.type().name(),
                model.status().name(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}