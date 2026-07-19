package com.company.scopery.modules.airecommendation.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EditSuggestionCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        long expectedVersion,
        String idempotencyKey,
        List<EditItemCommand> items,
        String comment,
        String traceId
) {
    public record EditItemCommand(UUID itemId, Map<String, Object> proposedPayload) {}
}
