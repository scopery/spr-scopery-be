package com.company.scopery.modules.aiagent.capability.application.response;

import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelParameterCapabilityDetailResponse(
        UUID id,
        UUID modelId,
        String modelName,
        String parameterName,
        String apiParameterKey,
        String supportStatus,
        String valueType,
        BigDecimal minValue,
        BigDecimal maxValue,
        String defaultValue,
        boolean nullable,
        String ifNullBehavior,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ModelParameterCapabilityDetailResponse from(ModelParameterCapability capability, String modelName) {
        return new ModelParameterCapabilityDetailResponse(
                capability.id(),
                capability.modelId(),
                modelName,
                capability.parameterName().value(),
                capability.apiParameterKey(),
                capability.supportStatus().name(),
                capability.valueType().name(),
                capability.minValue(),
                capability.maxValue(),
                capability.defaultValue(),
                capability.nullable(),
                capability.ifNullBehavior() != null ? capability.ifNullBehavior().name() : null,
                capability.description(),
                capability.status().name(),
                capability.createdAt(),
                capability.updatedAt()
        );
    }
}
