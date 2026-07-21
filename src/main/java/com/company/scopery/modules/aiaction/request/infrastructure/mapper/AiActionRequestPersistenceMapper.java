package com.company.scopery.modules.aiaction.request.infrastructure.mapper;

import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionRequestStatus;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.infrastructure.persistence.AiActionRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionRequestPersistenceMapper {

    public AiActionRequestJpaEntity toJpaEntity(AiActionRequest domain) {
        AiActionRequestJpaEntity entity = new AiActionRequestJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setInitiatedByUserId(domain.initiatedByUserId());
        entity.setOriginType(domain.originType().name());
        entity.setOriginConversationId(domain.originConversationId());
        entity.setOriginMessageId(domain.originMessageId());
        entity.setOriginTurnId(domain.originTurnId());
        entity.setOriginSuggestionRef(domain.originSuggestionRef());
        entity.setLegacyPhase21SuggestionId(domain.legacyPhase21SuggestionId());
        entity.setIntentSummary(domain.intentSummary());
        entity.setStatus(domain.status().name());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setRequestHash(domain.requestHash());
        entity.setLatestPlanId(domain.latestPlanId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public AiActionRequest toDomain(AiActionRequestJpaEntity entity) {
        return AiActionRequest.reconstitute(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getInitiatedByUserId(),
                AiActionOriginType.valueOf(entity.getOriginType()),
                entity.getOriginConversationId(),
                entity.getOriginMessageId(),
                entity.getOriginTurnId(),
                entity.getOriginSuggestionRef(),
                entity.getLegacyPhase21SuggestionId(),
                entity.getIntentSummary(),
                AiActionRequestStatus.valueOf(entity.getStatus()),
                entity.getIdempotencyKey(),
                entity.getRequestHash(),
                entity.getLatestPlanId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
