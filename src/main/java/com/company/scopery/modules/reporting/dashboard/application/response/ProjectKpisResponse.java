package com.company.scopery.modules.reporting.dashboard.application.response;

public record ProjectKpisResponse(
        TaskRiskReportResponse taskRisk,
        ProjectHealthResponse health,
        AiPlanningReportResponse aiPlanning
) {}
