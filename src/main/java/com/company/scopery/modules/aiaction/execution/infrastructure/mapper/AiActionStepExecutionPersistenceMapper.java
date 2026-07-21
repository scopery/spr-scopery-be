package com.company.scopery.modules.aiaction.execution.infrastructure.mapper;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionStepStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.infrastructure.persistence.AiActionStepExecutionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionStepExecutionPersistenceMapper {

    public AiActionStepExecutionJpaEntity toJpaEntity(AiActionStepExecution domain) {
        AiActionStepExecutionJpaEntity entity = new AiActionStepExecutionJpaEntity();
        entity.setId(domain.id());
        entity.setExecutionId(domain.executionId());
        entity.setStepId(domain.stepId());
        entity.setOrdinal(domain.ordinal());
        entity.setToolCode(domain.toolCode());
        entity.setAttempt(domain.attempt());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setStatus(domain.status().name());
        entity.setSafeResultSummaryJson(domain.safeResultSummaryJson());
        entity.setDomainResultRef(domain.domainResultRef());
        entity.setResultVersionToken(domain.resultVersionToken());
        entity.setErrorCode(domain.errorCode());
        entity.setRetryable(domain.retryable());
        entity.setAuditRef(domain.auditRef());
        entity.setOutboxRef(domain.outboxRef());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        return entity;
    }

    public AiActionStepExecution toDomain(AiActionStepExecutionJpaEntity entity) {
        return AiActionStepExecution.reconstitute(
                entity.getId(),
                entity.getExecutionId(),
                entity.getStepId(),
                entity.getOrdinal(),
                entity.getToolCode(),
                entity.getAttempt(),
                entity.getIdempotencyKey(),
                AiActionStepStatus.valueOf(entity.getStatus()),
                entity.getSafeResultSummaryJson(),
                entity.getDomainResultRef(),
                entity.getResultVersionToken(),
                entity.getErrorCode(),
                entity.getRetryable(),
                entity.getAuditRef(),
                entity.getOutboxRef(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }
}
