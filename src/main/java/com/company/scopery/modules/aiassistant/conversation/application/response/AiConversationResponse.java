package com.company.scopery.modules.aiassistant.conversation.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiConversation;

import java.time.Instant;
import java.util.UUID;

public record AiConversationResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID ownerUserId,
        String conversationType,
        String capabilityLevel,
        String status,
        String title,
        Instant lastMessageAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiConversationResponse from(AiConversation c) {
        return new AiConversationResponse(
                c.id(),
                c.workspaceId(),
                c.projectId(),
                c.ownerUserId(),
                c.conversationType().name(),
                c.capabilityLevel().name(),
                c.status().name(),
                c.title(),
                c.lastMessageAt(),
                c.createdAt(),
                c.updatedAt()
        );
    }
}
