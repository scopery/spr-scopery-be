package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceLabel;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionSeverity;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionPersistenceMapper {

    public AiSuggestion toDomain(AiSuggestionJpaEntity entity) {
        return new AiSuggestion(
                entity.getId(),
                entity.getRunId(),
                entity.getPolicyId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getSourceSystem() != null ? SourceSystem.valueOf(entity.getSourceSystem()) : null,
                entity.getLegacyPhase21SuggestionId(),
                entity.getPackCode(),
                entity.getDetectorCode(),
                entity.getSuggestionType(),
                entity.getSchemaCode(),
                entity.getSchemaVersion(),
                entity.getCategory(),
                SuggestionSeverity.valueOf(entity.getSeverity()),
                SuggestionStatus.valueOf(entity.getStatus()),
                entity.getTitle(),
                entity.getSummary(),
                entity.getReason(),
                entity.getTargetEntityType(),
                entity.getTargetEntityId(),
                entity.getTargetVersionToken(),
                entity.getConfidenceMethod() != null ? ConfidenceMethod.valueOf(entity.getConfidenceMethod()) : null,
                entity.getConfidenceValue(),
                entity.getConfidenceLabel() != null ? ConfidenceLabel.valueOf(entity.getConfidenceLabel()) : null,
                entity.getRiskLevel(),
                entity.getDedupKey(),
                entity.getPayloadHash(),
                entity.getOccurrenceCount(),
                entity.getOriginConversationId(),
                entity.getOriginMessageId(),
                entity.getOriginTurnId(),
                entity.getSupersedesSuggestionId(),
                entity.getSupersededBySuggestionId(),
                entity.getFirstObservedAt() != null ? entity.getFirstObservedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getLastObservedAt() != null ? entity.getLastObservedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getViewedAt() != null ? entity.getViewedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getEditedAt() != null ? entity.getEditedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getAcceptedAt() != null ? entity.getAcceptedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getRejectedAt() != null ? entity.getRejectedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getSuppressedAt() != null ? entity.getSuppressedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getExpiresAt() != null ? entity.getExpiresAt().atOffset(ZoneOffset.UTC) : null,
                entity.getStaleAt() != null ? entity.getStaleAt().atOffset(ZoneOffset.UTC) : null,
                entity.getStaleReasonCode(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersion()
        );
    }

    public AiSuggestionJpaEntity toJpaEntity(AiSuggestion domain) {
        AiSuggestionJpaEntity entity = new AiSuggestionJpaEntity();
        entity.setId(domain.id());
        entity.setRunId(domain.runId());
        entity.setPolicyId(domain.policyId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setSourceSystem(domain.sourceSystem() != null ? domain.sourceSystem().name() : null);
        entity.setLegacyPhase21SuggestionId(domain.legacyPhase21SuggestionId());
        entity.setPackCode(domain.packCode());
        entity.setDetectorCode(domain.detectorCode());
        entity.setSuggestionType(domain.suggestionType());
        entity.setSchemaCode(domain.schemaCode());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setCategory(domain.category());
        entity.setSeverity(domain.severity().name());
        entity.setStatus(domain.status().name());
        entity.setTitle(domain.title());
        entity.setSummary(domain.summary());
        entity.setReason(domain.reason());
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setTargetEntityId(domain.targetEntityId());
        entity.setTargetVersionToken(domain.targetVersionToken());
        entity.setConfidenceMethod(domain.confidenceMethod() != null ? domain.confidenceMethod().name() : null);
        entity.setConfidenceValue(domain.confidenceValue());
        entity.setConfidenceLabel(domain.confidenceLabel() != null ? domain.confidenceLabel().name() : null);
        entity.setRiskLevel(domain.riskLevel());
        entity.setDedupKey(domain.dedupKey());
        entity.setPayloadHash(domain.payloadHash());
        entity.setOccurrenceCount(domain.occurrenceCount());
        entity.setOriginConversationId(domain.originConversationId());
        entity.setOriginMessageId(domain.originMessageId());
        entity.setOriginTurnId(domain.originTurnId());
        entity.setSupersedesSuggestionId(domain.supersedesSuggestionId());
        entity.setSupersededBySuggestionId(domain.supersededBySuggestionId());
        entity.setFirstObservedAt(domain.firstObservedAt() != null ? domain.firstObservedAt().toInstant() : null);
        entity.setLastObservedAt(domain.lastObservedAt() != null ? domain.lastObservedAt().toInstant() : null);
        entity.setViewedAt(domain.viewedAt() != null ? domain.viewedAt().toInstant() : null);
        entity.setEditedAt(domain.editedAt() != null ? domain.editedAt().toInstant() : null);
        entity.setAcceptedAt(domain.acceptedAt() != null ? domain.acceptedAt().toInstant() : null);
        entity.setRejectedAt(domain.rejectedAt() != null ? domain.rejectedAt().toInstant() : null);
        entity.setSuppressedAt(domain.suppressedAt() != null ? domain.suppressedAt().toInstant() : null);
        entity.setExpiresAt(domain.expiresAt() != null ? domain.expiresAt().toInstant() : null);
        entity.setStaleAt(domain.staleAt() != null ? domain.staleAt().toInstant() : null);
        entity.setStaleReasonCode(domain.staleReasonCode());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
