package com.company.scopery.modules.knowledge.source.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record KnowledgeSourceResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String sourceType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        String language,
        String classification,
        String status,
        List<String> aclTokens,
        Instant lastIndexedAt,
        Instant updatedAt
) {}
