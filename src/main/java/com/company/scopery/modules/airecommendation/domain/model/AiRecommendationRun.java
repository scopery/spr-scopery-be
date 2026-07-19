package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.RunStatus;
import com.company.scopery.modules.airecommendation.domain.enums.TriggerType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AiRecommendationRun(
        UUID id,
        UUID policyId,
        UUID workspaceId,
        UUID projectId,
        UUID requestedBy,
        TriggerType triggerType,
        String idempotencyKey,
        String requestHash,
        RunStatus status,
        List<String> requestedPackCodes,
        List<String> detectorCodes,
        UUID originConversationId,
        UUID originMessageId,
        UUID originTurnId,
        int detectorCount,
        int candidateCount,
        int persistedCount,
        int deduplicatedCount,
        int suppressedCount,
        int discardedCount,
        int failedDetectorCount,
        Integer latencyMs,
        String errorCode,
        String errorSummaryRedacted,
        String traceId,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version
) {}
