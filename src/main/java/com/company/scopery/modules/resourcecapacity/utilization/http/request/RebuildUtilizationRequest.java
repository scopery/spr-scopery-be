package com.company.scopery.modules.resourcecapacity.utilization.http.request;

import java.math.BigDecimal;

public record RebuildUtilizationRequest(BigDecimal effortHours, BigDecimal availableCapacityHours) {}
