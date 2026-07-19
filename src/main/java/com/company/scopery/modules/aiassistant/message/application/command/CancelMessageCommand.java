package com.company.scopery.modules.aiassistant.message.application.command;

import java.util.UUID;

public record CancelMessageCommand(
        UUID messageId,
        UUID conversationId,
        UUID actorId
) {}
