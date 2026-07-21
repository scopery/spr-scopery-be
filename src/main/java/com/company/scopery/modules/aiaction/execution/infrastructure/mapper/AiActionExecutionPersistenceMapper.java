package com.company.scopery.modules.aiaction.execution.infrastructure.mapper;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.infrastructure.persistence.AiActionExecutionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiActionExecutionPersistenceMapper {

    public AiActionExecutionJpaEntity toJpaEntity(AiActionExecution domain) {
        AiActionExecutionJpaEntity entity = new AiActionExecutionJpaEntity();
        entity.setId(domain.id());
        entity.setPlanId(domain.planId());
        entity.setInitiatedByUserId(domain.initiatedByUserId());
        entity.setExecutionKey(domain.executionKey());
        entity.setStatus(domain.status().name());
        entity.setExecutionVersion(domain.executionVersion());
        entity.setWorkerInstanceId(domain.workerInstanceId());
        entity.setLeaseExpiresAt(domain.leaseExpiresAt());
        entity.setCurrentStepOrdinal(domain.currentStepOrdinal());
        entity.setSucceededCount(domain.succeededCount());
        entity.setFailedCount(domain.failedCount());
        entity.setSkippedCount(domain.skippedCount());
        entity.setCompensatedCount(domain.compensatedCount());
        entity.setCancelledCount(domain.cancelledCount());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public AiActionExecution toDomain(AiActionExecutionJpaEntity entity) {
        return AiActionExecution.reconstitute(
                (UUID) entity.getId(),
                entity.getPlanId(),
                entity.getInitiatedByUserId(),
                entity.getExecutionKey(),
                AiActionExecutionStatus.valueOf(entity.getStatus()),
                entity.getExecutionVersion(),
                entity.getWorkerInstanceId(),
                entity.getLeaseExpiresAt(),
                entity.getCurrentStepOrdinal(),
                entity.getSucceededCount(),
                entity.getFailedCount(),
                entity.getSkippedCount(),
                entity.getCompensatedCount(),
                entity.getCancelledCount(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
