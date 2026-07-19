package com.company.scopery.modules.airecommendation.application.query;

import java.util.UUID;

public record GetNextBestActionsQuery(
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        String suggestionRef,
        String entityType,
        UUID entityId,
        int limit
) {}
