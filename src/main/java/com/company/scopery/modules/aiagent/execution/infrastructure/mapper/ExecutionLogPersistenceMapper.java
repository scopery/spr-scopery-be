package com.company.scopery.modules.aiagent.execution.infrastructure.mapper;

import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;
import com.company.scopery.modules.aiagent.execution.infrastructure.persistence.entity.ExecutionLogJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExecutionLogPersistenceMapper {

    public ExecutionLogJpaEntity toJpaEntity(ExecutionLog log) {
        ExecutionLogJpaEntity entity = new ExecutionLogJpaEntity();
        entity.setId(log.id());
        entity.setRequestId(log.requestId().value());
        entity.setEventConfigId(log.eventConfigId());
        entity.setEventDefinitionId(log.eventDefinitionId());
        entity.setAgentId(log.agentId());
        entity.setPromptVersionId(log.promptVersionId());
        entity.setPromptTemplateId(log.promptTemplateId());
        entity.setModelDeploymentId(log.modelDeploymentId());
        entity.setProviderId(log.providerId());
        entity.setModelId(log.modelId());
        entity.setEnvironment(log.environment());
        entity.setTriggeredByUserId(log.triggeredByUserId());
        entity.setInputHash(log.inputHash());
        entity.setInputPreviewJson(log.inputPreviewJson());
        entity.setOutputPreviewJson(log.outputPreviewJson());
        entity.setCurrency(log.currency());
        entity.setTraceId(log.traceId());
        entity.setBlockReasonCode(log.blockReasonCode());
        entity.setTriggerSource(log.triggerSource().name());
        entity.setStatus(log.status().name());
        entity.setStartedAt(log.startedAt());
        entity.setCompletedAt(log.completedAt());
        entity.setLatencyMs(log.latencyMs());
        entity.setInputTokenCount(log.inputTokenCount());
        entity.setOutputTokenCount(log.outputTokenCount());
        entity.setTotalTokenCount(log.totalTokenCount());
        entity.setEstimatedCost(log.estimatedCost());
        entity.setProviderRequestId(log.providerRequestId());
        entity.setErrorCode(log.errorCode());
        entity.setErrorMessage(log.errorMessage());
        entity.setMetadata(log.metadata());
        if (log.createdAt() != null) {
            entity.setCreatedAt(log.createdAt());
        }
        return entity;
    }

    public ExecutionLog toDomain(ExecutionLogJpaEntity entity) {
        return ExecutionLog.reconstitute(
                entity.getId(),
                ExecutionRequestId.of(entity.getRequestId()),
                entity.getEventConfigId(),
                entity.getEventDefinitionId(),
                entity.getAgentId(),
                entity.getPromptVersionId(),
                entity.getPromptTemplateId(),
                entity.getModelDeploymentId(),
                entity.getProviderId(),
                entity.getModelId(),
                entity.getEnvironment(),
                entity.getTriggeredByUserId(),
                entity.getInputHash(),
                entity.getInputPreviewJson(),
                entity.getOutputPreviewJson(),
                entity.getCurrency(),
                entity.getTraceId(),
                entity.getBlockReasonCode(),
                ExecutionTriggerSource.valueOf(entity.getTriggerSource()),
                ExecutionStatus.valueOf(entity.getStatus()),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getLatencyMs(),
                entity.getInputTokenCount(),
                entity.getOutputTokenCount(),
                entity.getTotalTokenCount(),
                entity.getEstimatedCost(),
                entity.getProviderRequestId(),
                entity.getErrorCode(),
                entity.getErrorMessage(),
                entity.getMetadata(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
