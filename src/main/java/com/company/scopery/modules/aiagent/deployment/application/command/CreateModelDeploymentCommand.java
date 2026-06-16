package com.company.scopery.modules.aiagent.deployment.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateModelDeploymentCommand(
        UUID modelId,
        String name,
        String code,
        String environment,
        String providerDeploymentId,
        String endpointUrl,
        BigDecimal defaultTemperature,
        Integer defaultMaxOutputTokens,
        boolean isDefault,
        String description
) {}
