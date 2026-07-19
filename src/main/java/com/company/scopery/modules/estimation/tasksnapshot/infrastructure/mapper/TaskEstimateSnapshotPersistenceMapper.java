package com.company.scopery.modules.estimation.tasksnapshot.infrastructure.mapper;

import com.company.scopery.modules.estimation.tasksnapshot.domain.enums.TaskSnapshotStatus;
import com.company.scopery.modules.estimation.tasksnapshot.domain.model.TaskEstimateSnapshot;
import com.company.scopery.modules.estimation.tasksnapshot.infrastructure.persistence.TaskEstimateSnapshotJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskEstimateSnapshotPersistenceMapper {
    public TaskEstimateSnapshot toDomain(TaskEstimateSnapshotJpaEntity e) {
        return new TaskEstimateSnapshot(
                e.getId(), e.getEstimationRunId(), e.getProjectId(), e.getProjectPhaseId(), e.getWbsNodeId(),
                e.getTaskId(), e.getTaskCode(), e.getTaskTitle(), e.getAssigneeUserId(), e.getWorkspaceMemberId(),
                e.getCostRoleId(), e.getCostRoleCode(), e.getEstimateHours(), e.getRateTargetDate(),
                e.getRateCardId(), e.getRateCardVersionId(), e.getRateCardLineId(),
                e.getBaseCostRate(), e.getAdjustedCostRate(), e.getBaseBillingRate(), e.getAdjustedBillingRate(),
                e.getCurrencyCode(), e.getInflationPolicyId(), e.getInflationPercent(), e.getYearsForward(),
                e.getResolvedAt(), e.getEstimatedLaborCost(), e.getEstimatedBillingPreview(),
                TaskSnapshotStatus.valueOf(e.getStatus()), e.getIssueCode(), e.getIssueMessage(), e.getCreatedAt());
    }

    public TaskEstimateSnapshotJpaEntity toJpaEntity(TaskEstimateSnapshot d) {
        TaskEstimateSnapshotJpaEntity e = new TaskEstimateSnapshotJpaEntity();
        e.setId(d.id());
        e.setEstimationRunId(d.estimationRunId());
        e.setProjectId(d.projectId());
        e.setProjectPhaseId(d.projectPhaseId());
        e.setWbsNodeId(d.wbsNodeId());
        e.setTaskId(d.taskId());
        e.setTaskCode(d.taskCode());
        e.setTaskTitle(d.taskTitle());
        e.setAssigneeUserId(d.assigneeUserId());
        e.setWorkspaceMemberId(d.workspaceMemberId());
        e.setCostRoleId(d.costRoleId());
        e.setCostRoleCode(d.costRoleCode());
        e.setEstimateHours(d.estimateHours());
        e.setRateTargetDate(d.rateTargetDate());
        e.setRateCardId(d.rateCardId());
        e.setRateCardVersionId(d.rateCardVersionId());
        e.setRateCardLineId(d.rateCardLineId());
        e.setBaseCostRate(d.baseCostRate());
        e.setAdjustedCostRate(d.adjustedCostRate());
        e.setBaseBillingRate(d.baseBillingRate());
        e.setAdjustedBillingRate(d.adjustedBillingRate());
        e.setCurrencyCode(d.currencyCode());
        e.setInflationPolicyId(d.inflationPolicyId());
        e.setInflationPercent(d.inflationPercent());
        e.setYearsForward(d.yearsForward());
        e.setResolvedAt(d.resolvedAt());
        e.setEstimatedLaborCost(d.estimatedLaborCost());
        e.setEstimatedBillingPreview(d.estimatedBillingPreview());
        e.setStatus(d.status().name());
        e.setIssueCode(d.issueCode());
        e.setIssueMessage(d.issueMessage());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
