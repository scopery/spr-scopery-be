package com.company.scopery.modules.aiassistant.feedback.application.command;

import java.util.UUID;

public record CreateFeedbackCommand(
        UUID conversationId,
        UUID messageId,
        UUID actorId,
        String rating,
        String reasonCode,
        String comment
) {}
