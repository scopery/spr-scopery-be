package com.company.scopery.modules.projectnotification.reminder.infrastructure.mapper;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.*;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderEmission;
import com.company.scopery.modules.projectnotification.reminder.domain.model.ProjectReminderRun;
import com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence.ProjectReminderEmissionJpaEntity;
import com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence.ProjectReminderRunJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectReminderPersistenceMapper {
    public ProjectReminderRun toDomain(ProjectReminderRunJpaEntity e) {
        return new ProjectReminderRun(
                e.getId(), e.getWorkspaceId(), ReminderRunType.valueOf(e.getRunType()),
                ReminderRunStatus.valueOf(e.getStatus()), e.getStartedAt(), e.getCompletedAt(),
                e.getResultSummaryJson(), e.getErrorCode(), e.getErrorMessage(), e.getTraceId(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectReminderRunJpaEntity toJpaEntity(ProjectReminderRun d) {
        ProjectReminderRunJpaEntity e = new ProjectReminderRunJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setRunType(d.runType().name());
        e.setStatus(d.status().name());
        e.setStartedAt(d.startedAt());
        e.setCompletedAt(d.completedAt());
        e.setResultSummaryJson(d.resultSummaryJson());
        e.setErrorCode(d.errorCode());
        e.setErrorMessage(d.errorMessage());
        e.setTraceId(d.traceId());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }

    public ProjectReminderEmission toDomain(ProjectReminderEmissionJpaEntity e) {
        return new ProjectReminderEmission(
                e.getId(), e.getReminderRunId(), e.getProjectId(), e.getTaskId(), e.getMilestoneId(),
                e.getRecipientUserId(), ReminderType.valueOf(e.getReminderType()), e.getReminderDate(),
                e.getDedupKey(), ReminderEmissionStatus.valueOf(e.getStatus()), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectReminderEmissionJpaEntity toJpaEntity(ProjectReminderEmission d) {
        ProjectReminderEmissionJpaEntity e = new ProjectReminderEmissionJpaEntity();
        e.setId(d.id());
        e.setReminderRunId(d.reminderRunId());
        e.setProjectId(d.projectId());
        e.setTaskId(d.taskId());
        e.setMilestoneId(d.milestoneId());
        e.setRecipientUserId(d.recipientUserId());
        e.setReminderType(d.reminderType().name());
        e.setReminderDate(d.reminderDate());
        e.setDedupKey(d.dedupKey());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
