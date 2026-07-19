package com.company.scopery.modules.aiplanning.reviewaction.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.reviewaction.domain.enums.ReviewActionType;
import com.company.scopery.modules.aiplanning.reviewaction.domain.model.AiPlanningReviewAction;
import com.company.scopery.modules.aiplanning.reviewaction.infrastructure.persistence.AiPlanningReviewActionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningReviewActionPersistenceMapper {
    public AiPlanningReviewAction toDomain(AiPlanningReviewActionJpaEntity e) {
        return new AiPlanningReviewAction(e.getId(), e.getSuggestionId(), e.getSuggestionItemId(),
                ReviewActionType.valueOf(e.getAction()), e.getActorUserId(), e.getReason(), e.getTraceId(), e.getCreatedAt());
    }
    public AiPlanningReviewActionJpaEntity toJpaEntity(AiPlanningReviewAction d) {
        AiPlanningReviewActionJpaEntity e = new AiPlanningReviewActionJpaEntity();
        e.setId(d.id()); e.setSuggestionId(d.suggestionId()); e.setSuggestionItemId(d.suggestionItemId());
        e.setAction(d.action().name()); e.setActorUserId(d.actorUserId()); e.setReason(d.reason());
        e.setTraceId(d.traceId()); e.setCreatedAt(d.createdAt());
        return e;
    }
}
