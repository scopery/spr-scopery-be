package com.company.scopery.modules.integrationhub.dashboard.application.response;
public record IntegrationDashboardResponse(long activeConnections, long degradedConnections,
        long importJobs, long exportJobs, long openDeadLetters,
        long webhookSubscriptions, long syncJobs) {}
