package com.company.scopery.modules.airecommendation.application.command;

import com.company.scopery.modules.airecommendation.domain.enums.TriggerType;

import java.util.List;
import java.util.UUID;

public record CreateRecommendationRunCommand(
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        String policyCode,
        List<String> packCodes,
        TriggerType triggerType,
        String idempotencyKey,
        UUID originConversationId,
        UUID originMessageId,
        UUID originTurnId,
        String traceId
) {}
