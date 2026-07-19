package com.company.scopery.modules.aiassistant.message.application.command;

import java.util.UUID;

public record SendMessageCommand(
        UUID conversationId,
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        String content,
        String idempotencyKey,
        String modelProvider,
        String modelName,
        String pageCode,
        String entityType,
        UUID entityId
) {}
