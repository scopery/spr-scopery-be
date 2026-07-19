package com.company.scopery.modules.airecommendation.application.command;

import java.util.UUID;

public record RejectSuggestionCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        long expectedVersion,
        String idempotencyKey,
        String reasonCode,
        String comment,
        String traceId
) {}
