package com.company.scopery.modules.aiagent.capability.application.query;

import java.util.UUID;

public record SearchModelParameterCapabilityQuery(
        UUID modelId,
        String parameterName,
        String supportStatus,
        String valueType,
        String status,
        int page,
        int size
) {}