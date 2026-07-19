package com.company.scopery.modules.project.gantt.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record GanttViewQuery(
        UUID projectId,
        UUID scheduleRunId,
        LocalDate dateFrom,
        LocalDate dateTo,
        boolean includeUnscheduled,
        boolean includeArchived,
        String groupBy
) {}
