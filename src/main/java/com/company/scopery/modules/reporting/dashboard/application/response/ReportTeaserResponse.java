package com.company.scopery.modules.reporting.dashboard.application.response;

public record ReportTeaserResponse(
        Boolean available,
        Boolean detailsRedacted,
        String reason,
        Object currentFinanceScenarioId,
        Object currentQuoteVersionId
) {}
