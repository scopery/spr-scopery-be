package com.company.scopery.modules.aiagent.aimodel.application.response;

import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;

import java.time.Instant;
import java.util.UUID;

public record AiModelDetailResponse(
        UUID id,
        UUID providerId,
        String providerName,
        String name,
        String code,
        String providerModelId,
        String type,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static AiModelDetailResponse from(AiModel model, String providerName) {
        return new AiModelDetailResponse(
                model.id(),
                model.providerId(),
                providerName,
                model.name(),
                model.code().value(),
                model.providerModelId(),
                model.type().name(),
                model.description(),
                model.status().name(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}