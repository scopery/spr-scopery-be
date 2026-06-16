package com.company.scopery.modules.aiagent.aimodel.application.command;

import java.util.UUID;

public record UpdateAiModelCommand(
        UUID id,
        String name,
        String providerModelId,
        String type,
        String description
) {}
