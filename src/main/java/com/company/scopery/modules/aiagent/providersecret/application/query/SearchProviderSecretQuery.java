package com.company.scopery.modules.aiagent.providersecret.application.query;

import java.util.UUID;

public record SearchProviderSecretQuery(
        UUID providerId,
        String secretType,
        String status,
        int page,
        int size
) {}
