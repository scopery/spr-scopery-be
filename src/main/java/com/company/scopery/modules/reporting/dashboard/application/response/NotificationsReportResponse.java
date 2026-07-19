package com.company.scopery.modules.reporting.dashboard.application.response;

public record NotificationsReportResponse(
        Long overdueAlerts,
        Long dueSoonAlerts,
        Long scheduleRiskAlerts,
        Long changeRequestAlerts,
        Long unreadNotifications,
        Long criticalAlerts,
        Long failedDeliveries
) {}
