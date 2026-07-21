package com.company.scopery.modules.aiaction.application.port;

import java.util.List;
import java.util.UUID;

public record AiActionSuggestionData(
        UUID suggestionId,
        UUID workspaceId,
        UUID projectId,
        String intentSummary,
        List<AiActionRequestedAction> requestedActions
) {}
