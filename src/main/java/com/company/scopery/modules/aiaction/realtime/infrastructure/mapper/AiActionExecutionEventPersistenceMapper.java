package com.company.scopery.modules.aiaction.realtime.infrastructure.mapper;

import com.company.scopery.modules.aiaction.realtime.domain.enums.AiActionEventType;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;
import com.company.scopery.modules.aiaction.realtime.infrastructure.persistence.AiActionExecutionEventJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionExecutionEventPersistenceMapper {

    public AiActionExecutionEventJpaEntity toJpaEntity(AiActionExecutionEvent domain) {
        AiActionExecutionEventJpaEntity entity = new AiActionExecutionEventJpaEntity();
        entity.setId(domain.id());
        entity.setExecutionId(domain.executionId());
        entity.setSequence(domain.sequence());
        entity.setExecutionVersion(domain.executionVersion());
        entity.setEventType(domain.eventType().name());
        entity.setOccurredAt(domain.occurredAt());
        entity.setTraceId(domain.traceId());
        entity.setPayloadJson(domain.payloadJson());
        entity.setRedisPublishedAt(domain.redisPublishedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionExecutionEvent toDomain(AiActionExecutionEventJpaEntity entity) {
        return AiActionExecutionEvent.reconstitute(
                entity.getId(),
                entity.getExecutionId(),
                entity.getSequence(),
                entity.getExecutionVersion(),
                AiActionEventType.valueOf(entity.getEventType()),
                entity.getOccurredAt(),
                entity.getTraceId(),
                entity.getPayloadJson(),
                entity.getRedisPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
