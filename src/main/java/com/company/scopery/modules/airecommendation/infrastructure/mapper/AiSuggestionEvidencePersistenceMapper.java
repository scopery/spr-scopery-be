package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.enums.EvidenceType;
import com.company.scopery.modules.airecommendation.domain.enums.SupportStrength;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionEvidenceJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionEvidencePersistenceMapper {

    public AiSuggestionEvidence toDomain(AiSuggestionEvidenceJpaEntity entity) {
        return new AiSuggestionEvidence(
                entity.getId(),
                entity.getSuggestionId(),
                entity.getOrdinal(),
                EvidenceType.valueOf(entity.getEvidenceType()),
                entity.getSupportStrength() != null ? SupportStrength.valueOf(entity.getSupportStrength()) : null,
                entity.getAiassistantCitationId(),
                entity.getKnowledgeChunkId(),
                entity.getRetrievalTraceId(),
                entity.getSourceType(),
                entity.getSourceRefId(),
                entity.getSourceVersionRefId(),
                entity.getFieldPath(),
                entity.getTitle(),
                entity.getQuotedFragment(),
                entity.getAppRoute(),
                entity.getPermissionSignature(),
                entity.getAccessValidationResult() != null
                        ? AccessValidationResult.valueOf(entity.getAccessValidationResult()) : null,
                entity.getAccessValidatedAt() != null
                        ? entity.getAccessValidatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }

    public AiSuggestionEvidenceJpaEntity toJpaEntity(AiSuggestionEvidence domain) {
        AiSuggestionEvidenceJpaEntity entity = new AiSuggestionEvidenceJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setOrdinal(domain.ordinal());
        entity.setEvidenceType(domain.evidenceType().name());
        entity.setSupportStrength(domain.supportStrength() != null ? domain.supportStrength().name() : null);
        entity.setAiassistantCitationId(domain.aiassistantCitationId());
        entity.setKnowledgeChunkId(domain.knowledgeChunkId());
        entity.setRetrievalTraceId(domain.retrievalTraceId());
        entity.setSourceType(domain.sourceType());
        entity.setSourceRefId(domain.sourceRefId());
        entity.setSourceVersionRefId(domain.sourceVersionRefId());
        entity.setFieldPath(domain.fieldPath());
        entity.setTitle(domain.title());
        entity.setQuotedFragment(domain.quotedFragment());
        entity.setAppRoute(domain.appRoute());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setAccessValidationResult(
                domain.accessValidationResult() != null ? domain.accessValidationResult().name() : null);
        entity.setAccessValidatedAt(
                domain.accessValidatedAt() != null ? domain.accessValidatedAt().toInstant() : null);
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
