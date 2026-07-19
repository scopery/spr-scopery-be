package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.SuppressionScopeType;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionSuppression;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionSuppressionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionSuppressionPersistenceMapper {

    public AiSuggestionSuppression toDomain(AiSuggestionSuppressionJpaEntity entity) {
        return new AiSuggestionSuppression(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getActorId(),
                SuppressionScopeType.valueOf(entity.getScopeType()),
                entity.getScopeKey(),
                entity.getSuppressionKey(),
                entity.getTargetEntityType(),
                entity.getTargetEntityId(),
                entity.getSuggestionType(),
                entity.getPackCode(),
                entity.getReasonCode(),
                entity.getComment(),
                entity.isActive(),
                entity.getStartsAt() != null ? entity.getStartsAt().atOffset(ZoneOffset.UTC) : null,
                entity.getExpiresAt() != null ? entity.getExpiresAt().atOffset(ZoneOffset.UTC) : null,
                entity.getRevokedAt() != null ? entity.getRevokedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersion()
        );
    }

    public AiSuggestionSuppressionJpaEntity toJpaEntity(AiSuggestionSuppression domain) {
        AiSuggestionSuppressionJpaEntity entity = new AiSuggestionSuppressionJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setActorId(domain.actorId());
        entity.setScopeType(domain.scopeType().name());
        entity.setScopeKey(domain.scopeKey());
        entity.setSuppressionKey(domain.suppressionKey());
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setTargetEntityId(domain.targetEntityId());
        entity.setSuggestionType(domain.suggestionType());
        entity.setPackCode(domain.packCode());
        entity.setReasonCode(domain.reasonCode());
        entity.setComment(domain.comment());
        entity.setActive(domain.active());
        entity.setStartsAt(domain.startsAt() != null ? domain.startsAt().toInstant() : null);
        entity.setExpiresAt(domain.expiresAt() != null ? domain.expiresAt().toInstant() : null);
        entity.setRevokedAt(domain.revokedAt() != null ? domain.revokedAt().toInstant() : null);
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
