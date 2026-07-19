package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record KnowledgeSourceSnapshot(
        UUID workspaceId,
        UUID projectId,
        KnowledgeSourceType sourceType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        String language,
        String classification,
        String normalizedText,
        Map<String, Object> structuredMetadata,
        List<String> aclTokens,
        String sourceAccessVersion,
        String appRoute,
        Instant sourceUpdatedAt
) {}
