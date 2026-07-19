package com.company.scopery.modules.knowledge.retrieval.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RetrievalTrace(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        String queryHash,
        String retrievalMode,
        int lexicalCandidateCount,
        int vectorCandidateCount,
        int graphCandidateCount,
        int returnedCount,
        int durationMs,
        String resultStatus,
        String errorCode,
        Instant createdAt,
        Instant expiresAt
) {}
