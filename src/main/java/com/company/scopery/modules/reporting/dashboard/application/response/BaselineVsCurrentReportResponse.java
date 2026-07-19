package com.company.scopery.modules.reporting.dashboard.application.response;

public record BaselineVsCurrentReportResponse(
        Boolean sourceAvailable,
        Object currentBaselineId,
        Integer baselineNumber,
        TaskCountDeltaResponse taskCountDelta,
        ScheduleFinishDeltaResponse scheduleFinishDelta,
        EstimateHoursDeltaResponse estimateHoursDelta,
        Long changeRequestCount,
        Long approvedChangeImpact,
        Long tasksWithStatusDiff,
        Long tasksWithEstimateDiff,
        FinanceDeltaResponse financeDelta,
        QuoteDeltaResponse quoteDelta
) {
    public record TaskCountDeltaResponse(
            Long currentTaskCount,
            Object baselineScheduleRunId,
            Object currentScheduleRunId
    ) {}

    public record ScheduleFinishDeltaResponse(
            Object baselineScheduleRunId,
            Object currentScheduleRunId
    ) {}

    public record EstimateHoursDeltaResponse(
            Object baselineEstimationRunId,
            Object currentEstimationRunId
    ) {}

    public record FinanceDeltaResponse(
            Boolean detailsRedacted,
            String reason,
            Object baselineFinanceScenarioId,
            Object currentFinanceScenarioId
    ) {}

    public record QuoteDeltaResponse(
            Boolean detailsRedacted,
            String reason,
            Object baselineQuoteVersionId,
            Object currentQuoteVersionId
    ) {}
}
