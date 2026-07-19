package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;

public record ChangeImpactReportResponse(
        Long changeRequestCount,
        Long submittedCount,
        Long approvedCount,
        Long rejectedCount,
        Long appliedCount,
        Long pendingApprovalCount,
        Integer totalScheduleImpactDays,
        BigDecimal totalEstimateHoursImpact,
        Long changeOrderCount,
        BigDecimal totalCostImpact,
        BigDecimal totalRevenueImpact,
        BigDecimal totalMarginImpact,
        Boolean financeDetailsRedacted
) {}
