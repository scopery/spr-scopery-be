package com.company.scopery.modules.airecommendation.application.query;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public record ListProjectSuggestionsQuery(
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        List<String> statusFilter,
        List<String> severityFilter,
        String packCode,
        String type,
        String targetEntityType,
        boolean includeLegacyPhase21,
        boolean includeExpired,
        Pageable pageable
) {}
