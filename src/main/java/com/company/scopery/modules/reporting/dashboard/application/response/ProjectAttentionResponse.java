package com.company.scopery.modules.reporting.dashboard.application.response;

public record ProjectAttentionResponse(
        Long overdueTasks,
        Long blockedTasks,
        Long dueSoonTasks,
        Long pendingAiReview,
        Long changeRequestCount
) {}
