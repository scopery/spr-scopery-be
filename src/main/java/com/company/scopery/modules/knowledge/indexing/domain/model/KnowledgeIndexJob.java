package com.company.scopery.modules.knowledge.indexing.domain.model;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobType;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeIndexJob(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID sourceId,
        UUID embeddingProfileId,
        IndexJobType jobType,
        IndexJobStatus jobStatus,
        String idempotencyKey,
        String targetIndexName,
        int attemptCount,
        int processedCount,
        int successCount,
        int failureCount,
        String errorCode,
        String errorMessageRedacted,
        Instant queuedAt,
        Instant startedAt,
        Instant completedAt,
        UUID createdBy
) {}
