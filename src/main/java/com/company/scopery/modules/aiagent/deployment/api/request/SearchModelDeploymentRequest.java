package com.company.scopery.modules.aiagent.deployment.api.request;

import java.util.UUID;

public record SearchModelDeploymentRequest(
        UUID modelId,
        String environment,
        String keyword,
        String status,
        Boolean isDefault,
        int page,
        int size
) {}
