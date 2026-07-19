package com.company.scopery.modules.reporting.dashboard.application.response;

public record AiPlanningReportResponse(
        Long aiPlanningRuns,
        Long generatedSuggestions,
        Long pendingReviewCount,
        Long acceptedSuggestions,
        Long rejectedSuggestions,
        Long appliedSuggestions
) {}
