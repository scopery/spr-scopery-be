package com.company.scopery.modules.project.gantt.application.response;

import java.time.LocalDate;
import java.util.UUID;

public record GanttCriticalPathTaskResponse(
        UUID taskId,
        String title,
        LocalDate plannedStartDate,
        LocalDate plannedFinishDate,
        int durationDays,
        LocalDate earliestStart,
        LocalDate earliestFinish,
        LocalDate latestStart,
        LocalDate latestFinish,
        long slackDays,
        boolean critical
) {}
