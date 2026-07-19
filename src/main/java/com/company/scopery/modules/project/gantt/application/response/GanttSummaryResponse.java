package com.company.scopery.modules.project.gantt.application.response;

public record GanttSummaryResponse(
        int itemCount,
        int taskCount,
        int scheduledTaskCount,
        int unscheduledTaskCount,
        int milestoneCount,
        int dependencyCount,
        int issueCount
) {}
