package com.company.scopery.modules.airecommendation.application.command;

import java.util.List;
import java.util.UUID;

public record PrepareApplyCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        long expectedVersion,
        List<UUID> selectedItemIds,
        String idempotencyKey,
        UUID originConversationId,
        String traceId
) {}
