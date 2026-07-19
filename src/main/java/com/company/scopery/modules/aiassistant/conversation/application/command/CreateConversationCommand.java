package com.company.scopery.modules.aiassistant.conversation.application.command;

import com.company.scopery.modules.aiassistant.domain.enums.CapabilityLevel;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;

import java.util.UUID;

public record CreateConversationCommand(
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        ConversationType conversationType,
        CapabilityLevel capabilityLevel,
        UUID assistantAgentId,
        String title
) {}
