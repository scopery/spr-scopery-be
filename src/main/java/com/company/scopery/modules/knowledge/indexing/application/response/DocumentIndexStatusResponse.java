package com.company.scopery.modules.knowledge.indexing.application.response;

import java.time.Instant;
import java.util.UUID;

public record DocumentIndexStatusResponse(
        UUID documentId,
        UUID projectId,
        boolean indexed,
        int totalChunks,
        int embeddedChunks,
        Instant lastIndexedAt
) {}
