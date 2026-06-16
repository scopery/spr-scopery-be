package com.company.scopery.modules.aiagent.deployment.application.response;

import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelDeploymentDetailResponse(
        UUID id,
        UUID modelId,
        String modelName,
        String name,
        String code,
        String environment,
        String providerDeploymentId,
        String endpointUrl,
        BigDecimal defaultTemperature,
        Integer defaultMaxOutputTokens,
        boolean isDefault,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ModelDeploymentDetailResponse from(ModelDeployment deployment, String modelName) {
        return new ModelDeploymentDetailResponse(
                deployment.id(),
                deployment.modelId(),
                modelName,
                deployment.name(),
                deployment.code().value(),
                deployment.environment().name(),
                deployment.providerDeploymentId(),
                deployment.endpointUrl(),
                deployment.defaultTemperature(),
                deployment.defaultMaxOutputTokens(),
                deployment.isDefault(),
                deployment.description(),
                deployment.status().name(),
                deployment.createdAt(),
                deployment.updatedAt()
        );
    }
}
