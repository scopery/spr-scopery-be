package com.company.scopery.modules.projectfinance.scenario.http.request;

import jakarta.validation.Valid;

import java.math.BigDecimal;

public record UpdateFinanceScenarioRequest(
        String name,
        String description,
        BigDecimal plannedRevenue,
        String revenueSplitMethod,
        @Valid CreateFinanceScenarioRequest.PolicyRequest contingency,
        @Valid CreateFinanceScenarioRequest.PolicyRequest overhead,
        BigDecimal targetMarginPercent,
        String assumptionsJson
) {}
