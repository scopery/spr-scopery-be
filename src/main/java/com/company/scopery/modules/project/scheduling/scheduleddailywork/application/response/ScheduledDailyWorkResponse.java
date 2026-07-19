package com.company.scopery.modules.project.scheduling.scheduleddailywork.application.response;

import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.ScheduledDailyWork;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledDailyWorkResponse(UUID id,UUID taskId,UUID userId,LocalDate workDate,
        BigDecimal plannedHours,BigDecimal capacityHours,BigDecimal remainingCapacityAfter) {
    public static ScheduledDailyWorkResponse from(ScheduledDailyWork w){return new ScheduledDailyWorkResponse(w.id(),w.taskId(),w.userId(),w.workDate(),w.plannedHours(),w.capacityHours(),w.remainingCapacityAfter());}
}
