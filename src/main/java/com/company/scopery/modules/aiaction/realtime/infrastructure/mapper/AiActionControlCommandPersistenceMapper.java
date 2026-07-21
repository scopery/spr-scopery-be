package com.company.scopery.modules.aiaction.realtime.infrastructure.mapper;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionControlCommandStatus;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionControlCommandType;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionControlCommand;
import com.company.scopery.modules.aiaction.realtime.infrastructure.persistence.AiActionControlCommandJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionControlCommandPersistenceMapper {

    public AiActionControlCommandJpaEntity toJpaEntity(AiActionControlCommand domain) {
        AiActionControlCommandJpaEntity entity = new AiActionControlCommandJpaEntity();
        entity.setId(domain.id());
        entity.setExecutionId(domain.executionId());
        entity.setCommandType(domain.commandType().name());
        entity.setIssuedByUserId(domain.issuedByUserId());
        entity.setExpectedExecutionVersion(domain.expectedExecutionVersion());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        entity.setProcessedAt(domain.processedAt());
        return entity;
    }

    public AiActionControlCommand toDomain(AiActionControlCommandJpaEntity entity) {
        return AiActionControlCommand.reconstitute(
                entity.getId(),
                entity.getExecutionId(),
                AiActionControlCommandType.valueOf(entity.getCommandType()),
                entity.getIssuedByUserId(),
                entity.getExpectedExecutionVersion(),
                entity.getIdempotencyKey(),
                AiActionControlCommandStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getProcessedAt()
        );
    }
}
