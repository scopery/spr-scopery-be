package com.company.scopery.modules.reporting.dashboard.application.response;

import java.util.UUID;

public record ProjectDashboardResponse(
        ProjectSummaryResponse project,
        TaskRiskReportResponse taskRisk,
        ProjectHealthResponse health,
        BaselineSummaryResponse baseline,
        ChangeRequestsSummaryResponse changeRequests,
        AiPlanningReportResponse aiPlanning,
        ReportTeaserResponse finance,
        ReportTeaserResponse quote
) {
    public record ProjectSummaryResponse(
            UUID id,
            String code,
            String name,
            String status
    ) {}

    public record BaselineSummaryResponse(
            Object currentBaselineId,
            Boolean hasCurrentBaseline
    ) {}

    public record ChangeRequestsSummaryResponse(
            Long count
    ) {}
}
