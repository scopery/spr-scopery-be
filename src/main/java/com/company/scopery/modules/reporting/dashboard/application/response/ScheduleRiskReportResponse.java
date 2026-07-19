package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;
import java.util.Map;

public record ScheduleRiskReportResponse(
        Object currentScheduleRunId,
        Boolean sourceAvailable,
        String status,
        String scheduleRunStatus,
        String forecastStartDate,
        String forecastFinishDate,
        Long scheduledTasks,
        Long unscheduledTasks,
        Long atRiskTasks,
        Long overdueTasks,
        BigDecimal dueDateCapacityGapTotal,
        Map<String, Long> issueCountBySeverity,
        Long dependencyCycleCount,
        Long noCapacityTaskCount
) {}
