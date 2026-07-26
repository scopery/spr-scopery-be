package com.company.scopery.modules.projectbaseline.baseline.application.response;

import java.math.BigDecimal;

public record BaselineSummaryDto(
        int phaseCount, int wbsCount, int taskCount, int dependencyCount, int milestoneCount,
        String plannedStartDate, String plannedEndDate,
        BigDecimal estimateHours,
        BigDecimal revenue, BigDecimal directCost, BigDecimal overhead, BigDecimal grossMargin, BigDecimal pbt,
        String currencyCode,
        BigDecimal totalQuotedAmount, BigDecimal targetMarginPercent
) {}
