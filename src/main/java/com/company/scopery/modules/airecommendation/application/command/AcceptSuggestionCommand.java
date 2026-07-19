package com.company.scopery.modules.airecommendation.application.command;

import java.util.UUID;

public record AcceptSuggestionCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        long expectedVersion,
        String idempotencyKey,
        String comment,
        String traceId
) {}
