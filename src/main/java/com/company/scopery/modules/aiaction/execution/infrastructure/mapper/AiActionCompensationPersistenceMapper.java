package com.company.scopery.modules.aiaction.execution.infrastructure.mapper;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionCompensationStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionCompensation;
import com.company.scopery.modules.aiaction.execution.infrastructure.persistence.AiActionCompensationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionCompensationPersistenceMapper {

    public AiActionCompensationJpaEntity toJpaEntity(AiActionCompensation domain) {
        AiActionCompensationJpaEntity entity = new AiActionCompensationJpaEntity();
        entity.setId(domain.id());
        entity.setExecutionId(domain.executionId());
        entity.setStepExecutionId(domain.stepExecutionId());
        entity.setRequestedByUserId(domain.requestedByUserId());
        entity.setToolCode(domain.toolCode());
        entity.setStatus(domain.status().name());
        entity.setComment(domain.comment());
        entity.setCreatedAt(domain.createdAt());
        entity.setCompletedAt(domain.completedAt());
        return entity;
    }

    public AiActionCompensation toDomain(AiActionCompensationJpaEntity entity) {
        return AiActionCompensation.reconstitute(
                entity.getId(),
                entity.getExecutionId(),
                entity.getStepExecutionId(),
                entity.getRequestedByUserId(),
                entity.getToolCode(),
                AiActionCompensationStatus.valueOf(entity.getStatus()),
                entity.getComment(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
