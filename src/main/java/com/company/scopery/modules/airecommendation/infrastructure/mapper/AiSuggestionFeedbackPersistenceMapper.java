package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionFeedback;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionFeedbackJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionFeedbackPersistenceMapper {

    public AiSuggestionFeedback toDomain(AiSuggestionFeedbackJpaEntity entity) {
        return new AiSuggestionFeedback(
                entity.getId(),
                entity.getSuggestionId(),
                entity.getActorId(),
                entity.getHelpful(),
                entity.getReasonCode(),
                entity.getComment(),
                entity.getObservedOutcome(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }

    public AiSuggestionFeedbackJpaEntity toJpaEntity(AiSuggestionFeedback domain) {
        AiSuggestionFeedbackJpaEntity entity = new AiSuggestionFeedbackJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setActorId(domain.actorId());
        entity.setHelpful(domain.helpful());
        entity.setReasonCode(domain.reasonCode());
        entity.setComment(domain.comment());
        entity.setObservedOutcome(domain.observedOutcome());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
