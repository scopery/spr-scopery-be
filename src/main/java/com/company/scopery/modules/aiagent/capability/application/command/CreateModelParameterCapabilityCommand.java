package com.company.scopery.modules.aiagent.capability.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateModelParameterCapabilityCommand(
        UUID modelId,
        String parameterName,
        String apiParameterKey,
        String supportStatus,
        String valueType,
        BigDecimal minValue,
        BigDecimal maxValue,
        String defaultValue,
        boolean nullable,
        String ifNullBehavior,
        String description
) {}
