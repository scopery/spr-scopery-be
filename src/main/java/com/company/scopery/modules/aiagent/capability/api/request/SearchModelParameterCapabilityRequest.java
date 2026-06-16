package com.company.scopery.modules.aiagent.capability.api.request;

import java.util.UUID;

public record SearchModelParameterCapabilityRequest(
        UUID modelId,
        String parameterName,
        String supportStatus,
        String valueType,
        String status,
        int page,
        int size
) {}
