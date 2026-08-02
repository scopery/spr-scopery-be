package com.company.scopery.modules.project.timeline.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimelineBucketResponse(
        LocalDate periodStart,
        LocalDate periodEnd,
        int plannedMinutes,
        BigDecimal plannedContributionPercent,
        BigDecimal cumulativePlannedPercent,
        BigDecimal actualProgressPercent,
        BigDecimal variancePercent
) {}
