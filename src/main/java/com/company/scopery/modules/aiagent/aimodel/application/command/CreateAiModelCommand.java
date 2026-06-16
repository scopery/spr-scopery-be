package com.company.scopery.modules.aiagent.aimodel.application.command;

import java.util.UUID;

public record CreateAiModelCommand(
        UUID providerId,
        String name,
        String code,
        String providerModelId,
        String type,
        String description
) {}
