package com.company.scopery.modules.knowledge.indexing.infrastructure.postgres;

import java.util.List;
import java.util.UUID;

public record KnowledgeChunkIndexRecord(
        UUID chunkId,
        UUID workspaceId,
        UUID projectId,
        String title,
        String content,
        float[] embedding,
        String language,
        String classification,
        String sourceType,
        String sourceStatus,
        List<String> aclTokens,
        String appRoute
) {}
