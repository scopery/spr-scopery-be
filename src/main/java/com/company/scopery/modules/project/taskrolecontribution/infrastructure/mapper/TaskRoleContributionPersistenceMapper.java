package com.company.scopery.modules.project.taskrolecontribution.infrastructure.mapper;

import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContribution;
import com.company.scopery.modules.project.taskrolecontribution.infrastructure.persistence.TaskRoleContributionJpaEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TaskRoleContributionPersistenceMapper {

    public TaskRoleContribution toDomain(TaskRoleContributionJpaEntity entity) {
        return new TaskRoleContribution(
                entity.getId(),
                entity.getProjectId(),
                entity.getTaskId(),
                entity.getUserId(),
                entity.getCostRoleCode(),
                entity.getCostRoleName(),
                entity.getPlannedHours(),
                entity.getActualHours() != null ? entity.getActualHours() : BigDecimal.ZERO,
                entity.getRateSnapshotPerHour(),
                entity.getCurrencyCode(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public TaskRoleContributionJpaEntity toJpaEntity(TaskRoleContribution domain) {
        TaskRoleContributionJpaEntity entity = new TaskRoleContributionJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setTaskId(domain.taskId());
        entity.setUserId(domain.userId());
        entity.setCostRoleCode(domain.costRoleCode());
        entity.setCostRoleName(domain.costRoleName());
        entity.setPlannedHours(domain.plannedHours());
        entity.setActualHours(domain.actualHours() != null ? domain.actualHours() : BigDecimal.ZERO);
        entity.setRateSnapshotPerHour(domain.rateSnapshotPerHour());
        entity.setCurrencyCode(domain.currencyCode());
        entity.setPeriodStart(domain.periodStart());
        entity.setPeriodEnd(domain.periodEnd());
        entity.setNotes(domain.notes());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
