package com.company.scopery.modules.aiassistant.conversation.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateConversationRequest(
        @NotNull UUID workspaceId,
        UUID projectId,
        @NotBlank String conversationType,
        @NotBlank String capabilityLevel,
        UUID assistantAgentId,
        @Size(max = 200) String title
) {}
