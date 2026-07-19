package com.company.scopery.modules.projectfinance.phasefinance.http.request;

import java.math.BigDecimal;

public record UpdatePhaseRevenueRequest(
        BigDecimal plannedRevenue,
        BigDecimal revenuePercent
) {}
