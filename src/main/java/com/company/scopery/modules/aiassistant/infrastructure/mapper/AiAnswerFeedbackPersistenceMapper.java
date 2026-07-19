package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedback;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiAnswerFeedbackJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiAnswerFeedbackPersistenceMapper {

    public AiAnswerFeedbackJpaEntity toJpaEntity(AiAnswerFeedback domain) {
        AiAnswerFeedbackJpaEntity entity = new AiAnswerFeedbackJpaEntity();
        entity.setId(domain.id());
        entity.setConversationId(domain.conversationId());
        entity.setMessageId(domain.messageId());
        entity.setActorId(domain.actorId());
        entity.setRating(domain.rating());
        entity.setReasonCode(domain.reasonCode());
        entity.setComment(domain.comment());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        entity.setVersion(domain.version());
        return entity;
    }

    public AiAnswerFeedback toDomain(AiAnswerFeedbackJpaEntity entity) {
        return AiAnswerFeedback.reconstitute(
                entity.getId(),
                entity.getConversationId(),
                entity.getMessageId(),
                entity.getActorId(),
                entity.getRating(),
                entity.getReasonCode(),
                entity.getComment(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
