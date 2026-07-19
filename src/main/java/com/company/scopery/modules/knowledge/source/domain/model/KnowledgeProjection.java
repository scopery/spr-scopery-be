package com.company.scopery.modules.knowledge.source.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record KnowledgeProjection(
        UUID id,
        UUID sourceId,
        int projectionVersion,
        String extractorCode,
        String extractorVersion,
        String normalizationVersion,
        String plainText,
        Map<String, Object> structuredMetadata,
        String contentHash,
        String projectionStatus,
        String failureCode,
        String failureMessageRedacted,
        Instant createdAt,
        UUID createdBy
) {}
