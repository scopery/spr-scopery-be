package com.company.scopery.modules.reporting.dashboard.application.response;

public record TaskRiskReportResponse(
        Long totalTasks,
        Long todoTasks,
        Long inProgressTasks,
        Long blockedTasks,
        Long completedTasks,
        Long cancelledTasks,
        Long overdueTasks,
        Long dueSoonTasks,
        Long unscheduledTasks,
        Long atRiskTasks,
        Long tasksWithoutEstimate,
        Long tasksWithoutAssignee
) {}
