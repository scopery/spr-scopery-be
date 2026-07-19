package com.company.scopery.modules.reporting.dashboard.application.response;

public record ProjectHealthResponse(
        String status,
        String formulaVersion,
        TaskRiskReportResponse drivers
) {}
