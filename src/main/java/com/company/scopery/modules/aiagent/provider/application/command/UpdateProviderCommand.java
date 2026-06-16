package com.company.scopery.modules.aiagent.provider.application.command;

import java.util.UUID;

public record UpdateProviderCommand(
        UUID id,
        String name,
        String type,
        String apiBaseUrl,
        String description
) {}