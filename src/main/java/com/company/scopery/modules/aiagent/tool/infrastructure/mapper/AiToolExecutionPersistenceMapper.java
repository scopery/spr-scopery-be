package com.company.scopery.modules.aiagent.tool.infrastructure.mapper;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolApprovalState;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolExecution;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolExecutionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiToolExecutionPersistenceMapper {

    public AiToolExecutionJpaEntity toJpaEntity(AiToolExecution execution) {
        AiToolExecutionJpaEntity entity = new AiToolExecutionJpaEntity();
        entity.setId(execution.id());
        entity.setToolId(execution.toolId());
        entity.setAgentId(execution.agentId());
        entity.setRequestedByUserId(execution.requestedByUserId());
        entity.setStatus(execution.status().name());
        entity.setApprovalState(execution.approvalState().name());
        entity.setInputSummary(execution.inputSummary());
        entity.setErrorMessage(execution.errorMessage());
        entity.setResultSummary(execution.resultSummary());
        entity.setStartedAt(execution.startedAt());
        entity.setFinishedAt(execution.finishedAt());
        if (execution.createdAt() != null) {
            entity.setCreatedAt(execution.createdAt());
        }
        return entity;
    }

    public AiToolExecution toDomain(AiToolExecutionJpaEntity entity) {
        return AiToolExecution.reconstitute(
                entity.getId(),
                entity.getToolId(),
                entity.getAgentId(),
                entity.getRequestedByUserId(),
                AiToolExecutionStatus.valueOf(entity.getStatus()),
                AiToolApprovalState.valueOf(entity.getApprovalState()),
                entity.getInputSummary(),
                entity.getErrorMessage(),
                entity.getResultSummary(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
