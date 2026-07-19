package com.company.scopery.modules.project.scheduling.taskschedule.application.response;

import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TaskScheduleResponse(UUID id,UUID scheduleRunId,UUID taskId,UUID assigneeUserId,
        LocalDate estimatedStartDate,LocalDate estimatedFinishDate,BigDecimal scheduledHours,
        BigDecimal unscheduledHours,LocalDate dueDate,BigDecimal dueDateCapacityGapHours,
        String riskStatus,String scheduleStatus) {
    public static TaskScheduleResponse from(TaskSchedule s){return new TaskScheduleResponse(s.id(),s.scheduleRunId(),s.taskId(),s.assigneeUserId(),s.estimatedStartDate(),s.estimatedFinishDate(),s.scheduledHours(),s.unscheduledHours(),s.dueDate(),s.dueDateCapacityGapHours(),s.riskStatus().name(),s.scheduleStatus().name());}
}
