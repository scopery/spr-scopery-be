package com.company.scopery.modules.knowledge.source.application.response;

import java.util.List;
import java.util.UUID;

public record KnowledgeChunkResponse(
        UUID id,
        UUID sourceId,
        int chunkOrdinal,
        String strategyVersion,
        String chunkType,
        List<String> headingPath,
        String plainText,
        int tokenCount,
        String contentHash,
        boolean isCurrent
) {}
