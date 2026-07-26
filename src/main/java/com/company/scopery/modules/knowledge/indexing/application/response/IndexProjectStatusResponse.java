package com.company.scopery.modules.knowledge.indexing.application.response;

import java.util.UUID;

public record IndexProjectStatusResponse(
        UUID projectId,
        int indexedSources,
        int totalChunks,
        int embeddedChunks,
        int missingEmbedding,
        boolean embeddingReady
) {}
