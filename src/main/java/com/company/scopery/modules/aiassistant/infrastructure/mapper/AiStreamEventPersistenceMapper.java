package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiStreamEvent;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiStreamEventJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiStreamEventPersistenceMapper {

    public AiStreamEventJpaEntity toJpaEntity(AiStreamEvent domain) {
        AiStreamEventJpaEntity entity = new AiStreamEventJpaEntity();
        entity.setId(domain.id());
        entity.setMessageId(domain.messageId());
        entity.setSequence(domain.sequence());
        entity.setEventType(domain.eventType());
        entity.setPayload(domain.payloadJson());
        entity.setPayloadHash(domain.payloadHash());
        entity.setCreatedAt(domain.createdAt());
        entity.setExpiresAt(domain.expiresAt());
        return entity;
    }

    public AiStreamEvent toDomain(AiStreamEventJpaEntity entity) {
        return AiStreamEvent.reconstitute(
                entity.getId(),
                entity.getMessageId(),
                entity.getSequence(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getPayloadHash(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }
}
