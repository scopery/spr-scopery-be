package com.company.scopery.modules.aiagent.deployment.application.response;

import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ModelDeploymentResponse(
        UUID id,
        UUID modelId,
        String name,
        String code,
        String environment,
        String providerDeploymentId,
        String endpointUrl,
        BigDecimal defaultTemperature,
        Integer defaultMaxOutputTokens,
        boolean isDefault,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ModelDeploymentResponse from(ModelDeployment deployment) {
        return new ModelDeploymentResponse(
                deployment.id(),
                deployment.modelId(),
                deployment.name(),
                deployment.code().value(),
                deployment.environment().name(),
                deployment.providerDeploymentId(),
                deployment.endpointUrl(),
                deployment.defaultTemperature(),
                deployment.defaultMaxOutputTokens(),
                deployment.isDefault(),
                deployment.status().name(),
                deployment.createdAt(),
                deployment.updatedAt()
        );
    }
}
