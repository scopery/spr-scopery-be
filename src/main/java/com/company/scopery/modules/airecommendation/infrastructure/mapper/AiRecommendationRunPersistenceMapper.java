package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.RunStatus;
import com.company.scopery.modules.airecommendation.domain.enums.TriggerType;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiRecommendationRunJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class AiRecommendationRunPersistenceMapper {

    public AiRecommendationRun toDomain(AiRecommendationRunJpaEntity entity) {
        return new AiRecommendationRun(
                entity.getId(),
                entity.getPolicyId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getRequestedBy(),
                TriggerType.valueOf(entity.getTriggerType()),
                entity.getIdempotencyKey(),
                entity.getRequestHash(),
                RunStatus.valueOf(entity.getStatus()),
                parseList(entity.getRequestedPackCodes()),
                parseList(entity.getDetectorCodes()),
                entity.getOriginConversationId(),
                entity.getOriginMessageId(),
                entity.getOriginTurnId(),
                entity.getDetectorCount(),
                entity.getCandidateCount(),
                entity.getPersistedCount(),
                entity.getDeduplicatedCount(),
                entity.getSuppressedCount(),
                entity.getDiscardedCount(),
                entity.getFailedDetectorCount(),
                entity.getLatencyMs(),
                entity.getErrorCode(),
                entity.getErrorSummaryRedacted(),
                entity.getTraceId(),
                entity.getStartedAt() != null ? entity.getStartedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getCompletedAt() != null ? entity.getCompletedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersion()
        );
    }

    public AiRecommendationRunJpaEntity toJpaEntity(AiRecommendationRun domain) {
        AiRecommendationRunJpaEntity entity = new AiRecommendationRunJpaEntity();
        entity.setId(domain.id());
        entity.setPolicyId(domain.policyId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setRequestedBy(domain.requestedBy());
        entity.setTriggerType(domain.triggerType().name());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setRequestHash(domain.requestHash());
        entity.setStatus(domain.status().name());
        entity.setRequestedPackCodes(joinList(domain.requestedPackCodes()));
        entity.setDetectorCodes(joinList(domain.detectorCodes()));
        entity.setOriginConversationId(domain.originConversationId());
        entity.setOriginMessageId(domain.originMessageId());
        entity.setOriginTurnId(domain.originTurnId());
        entity.setDetectorCount(domain.detectorCount());
        entity.setCandidateCount(domain.candidateCount());
        entity.setPersistedCount(domain.persistedCount());
        entity.setDeduplicatedCount(domain.deduplicatedCount());
        entity.setSuppressedCount(domain.suppressedCount());
        entity.setDiscardedCount(domain.discardedCount());
        entity.setFailedDetectorCount(domain.failedDetectorCount());
        entity.setLatencyMs(domain.latencyMs());
        entity.setErrorCode(domain.errorCode());
        entity.setErrorSummaryRedacted(domain.errorSummaryRedacted());
        entity.setTraceId(domain.traceId());
        entity.setStartedAt(domain.startedAt() != null ? domain.startedAt().toInstant() : null);
        entity.setCompletedAt(domain.completedAt() != null ? domain.completedAt().toInstant() : null);
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }

    private List<String> parseList(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }
}
