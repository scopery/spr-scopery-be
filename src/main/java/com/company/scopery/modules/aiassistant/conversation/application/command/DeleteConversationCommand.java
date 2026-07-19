package com.company.scopery.modules.aiassistant.conversation.application.command;

import java.util.UUID;

public record DeleteConversationCommand(
        UUID conversationId,
        UUID actorId
) {}
