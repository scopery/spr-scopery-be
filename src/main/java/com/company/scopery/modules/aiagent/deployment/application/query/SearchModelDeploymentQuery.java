package com.company.scopery.modules.aiagent.deployment.application.query;

import java.util.UUID;

public record SearchModelDeploymentQuery(
        UUID modelId,
        String environment,
        String keyword,
        String status,
        Boolean isDefault,
        int page,
        int size
) {}
