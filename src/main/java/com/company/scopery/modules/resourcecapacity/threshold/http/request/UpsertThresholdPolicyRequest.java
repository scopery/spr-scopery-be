package com.company.scopery.modules.resourcecapacity.threshold.http.request;

import java.math.BigDecimal;

public record UpsertThresholdPolicyRequest(
        BigDecimal underAllocatedPercent,
        BigDecimal healthyMinPercent,
        BigDecimal healthyMaxPercent,
        BigDecimal watchMaxPercent,
        BigDecimal overloadedPercent,
        BigDecimal criticalOverloadPercent
) {}
