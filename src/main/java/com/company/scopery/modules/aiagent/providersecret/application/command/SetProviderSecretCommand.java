package com.company.scopery.modules.aiagent.providersecret.application.command;

import java.util.UUID;

public record SetProviderSecretCommand(
        UUID providerId,
        String secretType,
        String secretValue,
        String description
) {}
