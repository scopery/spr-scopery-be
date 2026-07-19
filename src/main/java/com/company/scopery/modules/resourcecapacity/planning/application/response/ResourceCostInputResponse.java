package com.company.scopery.modules.resourcecapacity.planning.application.response;
import java.math.BigDecimal; import java.util.List; import java.util.UUID;
public record ResourceCostInputResponse(UUID projectId, BigDecimal totalCostAmount, String currency, boolean rateMissing,
        List<Line> lines) {
    public record Line(UUID resourceRoleId, BigDecimal effortHours, BigDecimal rateAmount, BigDecimal costAmount, boolean masked) {}
}
