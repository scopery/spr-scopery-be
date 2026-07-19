package com.company.scopery.modules.project.scheduling.taskschedule.domain.model;

import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskSchedule(
        UUID id, UUID scheduleRunId, UUID projectId, UUID taskId, UUID assigneeUserId,
        UUID workspaceMemberId, LocalDate estimatedStartDate, LocalDate estimatedFinishDate,
        BigDecimal scheduledHours, BigDecimal unscheduledHours, LocalDate dueDate,
        BigDecimal dueDateCapacityGapHours, TaskScheduleRiskStatus riskStatus,
        TaskScheduleStatus scheduleStatus, Instant createdAt, Instant updatedAt) {

    public static TaskSchedule create(UUID runId, UUID projectId, UUID taskId, UUID userId, UUID memberId,
                                      LocalDate start, LocalDate finish, BigDecimal scheduled,
                                      BigDecimal unscheduled, LocalDate dueDate, BigDecimal gap,
                                      TaskScheduleRiskStatus risk, TaskScheduleStatus status) {
        return new TaskSchedule(UUID.randomUUID(), runId, projectId, taskId, userId, memberId, start, finish,
                scheduled, unscheduled, dueDate, gap, risk, status, null, null);
    }
}
