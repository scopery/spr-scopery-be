package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.ActiveStreamStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiActiveStream;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiActiveStreamJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActiveStreamPersistenceMapper {

    public AiActiveStreamJpaEntity toJpaEntity(AiActiveStream domain) {
        AiActiveStreamJpaEntity entity = new AiActiveStreamJpaEntity();
        entity.setId(domain.id());
        entity.setMessageId(domain.messageId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setActorId(domain.actorId());
        entity.setStreamStatus(domain.streamStatus() != null ? domain.streamStatus().name() : null);
        entity.setAcquiredAt(domain.acquiredAt());
        entity.setExpiresAt(domain.expiresAt());
        entity.setReleasedAt(domain.releasedAt());
        return entity;
    }

    public AiActiveStream toDomain(AiActiveStreamJpaEntity entity) {
        return AiActiveStream.reconstitute(
                entity.getId(),
                entity.getMessageId(),
                entity.getWorkspaceId(),
                entity.getActorId(),
                entity.getStreamStatus() != null ? ActiveStreamStatus.valueOf(entity.getStreamStatus()) : null,
                entity.getAcquiredAt(),
                entity.getExpiresAt(),
                entity.getReleasedAt()
        );
    }
}
