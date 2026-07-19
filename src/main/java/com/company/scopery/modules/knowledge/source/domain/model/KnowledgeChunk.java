package com.company.scopery.modules.knowledge.source.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record KnowledgeChunk(
        UUID id,
        UUID sourceId,
        UUID projectionId,
        int chunkOrdinal,
        String strategyVersion,
        String chunkType,
        List<String> headingPath,
        String plainText,
        int tokenCount,
        int startCodePoint,
        int endCodePoint,
        String contentHash,
        Map<String, Object> metadata,
        boolean isCurrent,
        Instant createdAt
) {}
