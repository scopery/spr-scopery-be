package com.company.scopery.modules.aiassistant.conversation.application.command;

import java.util.UUID;

public record UpdateConversationTitleCommand(
        UUID conversationId,
        UUID actorId,
        String title
) {}
