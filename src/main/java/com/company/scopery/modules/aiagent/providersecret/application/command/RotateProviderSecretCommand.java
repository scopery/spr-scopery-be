package com.company.scopery.modules.aiagent.providersecret.application.command;

import java.util.UUID;

public record RotateProviderSecretCommand(
        UUID id,
        String secretValue,
        String description
) {}
