package com.company.scopery.modules.aiplanning.planningrun.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunStatus;
import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;
import com.company.scopery.modules.aiplanning.planningrun.infrastructure.persistence.AiPlanningRunJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningRunPersistenceMapper {
    public AiPlanningRun toDomain(AiPlanningRunJpaEntity e) {
        return new AiPlanningRun(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getActorUserId(), e.getAgentId(),
                e.getAgentVersionId(), e.getPromptTemplateId(), e.getPromptTemplateVersionId(),
                e.getModelDeploymentId(), e.getAiExecutionLogId(),
                PlanningRunType.valueOf(e.getRunType()), PlanningRunStatus.valueOf(e.getStatus()),
                e.getInputSummaryJson(), e.getContextSnapshotId(), e.getOutputSummaryJson(),
                e.getErrorCode(), e.getErrorMessage(), e.getStartedAt(), e.getCompletedAt(), e.getTraceId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public AiPlanningRunJpaEntity toJpaEntity(AiPlanningRun d) {
        AiPlanningRunJpaEntity e = new AiPlanningRunJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setActorUserId(d.actorUserId());
        e.setAgentId(d.agentId());
        e.setAgentVersionId(d.agentVersionId());
        e.setPromptTemplateId(d.promptTemplateId());
        e.setPromptTemplateVersionId(d.promptTemplateVersionId());
        e.setModelDeploymentId(d.modelDeploymentId());
        e.setAiExecutionLogId(d.aiExecutionLogId());
        e.setRunType(d.runType().name());
        e.setStatus(d.status().name());
        e.setInputSummaryJson(d.inputSummaryJson());
        e.setContextSnapshotId(d.contextSnapshotId());
        e.setOutputSummaryJson(d.outputSummaryJson());
        e.setErrorCode(d.errorCode());
        e.setErrorMessage(d.errorMessage());
        e.setStartedAt(d.startedAt());
        e.setCompletedAt(d.completedAt());
        e.setTraceId(d.traceId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
