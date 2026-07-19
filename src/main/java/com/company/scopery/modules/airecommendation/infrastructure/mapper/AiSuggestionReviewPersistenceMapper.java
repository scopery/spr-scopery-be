package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionReviewJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionReviewPersistenceMapper {

    public AiSuggestionReview toDomain(AiSuggestionReviewJpaEntity entity) {
        return new AiSuggestionReview(
                entity.getId(),
                entity.getSuggestionId(),
                entity.getActorId(),
                ReviewDecision.valueOf(entity.getDecision()),
                SuggestionStatus.valueOf(entity.getFromStatus()),
                SuggestionStatus.valueOf(entity.getToStatus()),
                entity.getExpectedSuggestionVersion(),
                entity.getReasonCode(),
                entity.getComment(),
                entity.getEditedItemsJson(),
                entity.getTraceId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }

    public AiSuggestionReviewJpaEntity toJpaEntity(AiSuggestionReview domain) {
        AiSuggestionReviewJpaEntity entity = new AiSuggestionReviewJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setActorId(domain.actorId());
        entity.setDecision(domain.decision().name());
        entity.setFromStatus(domain.fromStatus().name());
        entity.setToStatus(domain.toStatus().name());
        entity.setExpectedSuggestionVersion(domain.expectedSuggestionVersion());
        entity.setReasonCode(domain.reasonCode());
        entity.setComment(domain.comment());
        entity.setEditedItemsJson(domain.editedItemsJson());
        entity.setTraceId(domain.traceId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
