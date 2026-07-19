package com.company.scopery.modules.scope.report.application.response;
public record ScopeCoverageReportResponse(long packageCount, long itemCount, long inScopeCount,
        long outOfScopeCount, long activeWbsMappingCount) {}
