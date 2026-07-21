package com.company.scopery.modules.aiaction.application.command;

import com.company.scopery.modules.aiaction.application.port.AiActionRequestedAction;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;

import java.util.List;
import java.util.UUID;

public record CreateAiActionRequestCommand(
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        AiActionOriginType originType,
        UUID originConversationId,
        UUID originMessageId,
        UUID originTurnId,
        String originSuggestionRef,
        String legacyPhase21SuggestionId,
        String intentSummary,
        List<AiActionRequestedAction> requestedActions,
        String idempotencyKey
) {}
