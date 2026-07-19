package com.company.scopery.modules.resourcecapacity.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ResourceUtilizationCalculator {
    private ResourceUtilizationCalculator() {}

    public static BigDecimal utilizationPercent(BigDecimal effortHours, BigDecimal availableCapacityHours) {
        if (availableCapacityHours == null || availableCapacityHours.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (effortHours == null) effortHours = BigDecimal.ZERO;
        return effortHours.multiply(new BigDecimal("100")).divide(availableCapacityHours, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal overloadHours(BigDecimal effortHours, BigDecimal availableCapacityHours) {
        if (availableCapacityHours == null) availableCapacityHours = BigDecimal.ZERO;
        if (effortHours == null) effortHours = BigDecimal.ZERO;
        BigDecimal overload = effortHours.subtract(availableCapacityHours);
        return overload.compareTo(BigDecimal.ZERO) > 0 ? overload : BigDecimal.ZERO;
    }

    public static BigDecimal underAllocationHours(BigDecimal effortHours, BigDecimal availableCapacityHours) {
        if (availableCapacityHours == null) availableCapacityHours = BigDecimal.ZERO;
        if (effortHours == null) effortHours = BigDecimal.ZERO;
        BigDecimal under = availableCapacityHours.subtract(effortHours);
        return under.compareTo(BigDecimal.ZERO) > 0 ? under : BigDecimal.ZERO;
    }

    public static BigDecimal costAmount(BigDecimal effortHours, BigDecimal rateAmount) {
        if (effortHours == null || rateAmount == null) return BigDecimal.ZERO;
        return effortHours.multiply(rateAmount).setScale(4, RoundingMode.HALF_UP);
    }

    public static String utilizationStatus(BigDecimal plannedPercent,
                                           BigDecimal underAllocated, BigDecimal healthyMin, BigDecimal healthyMax,
                                           BigDecimal watchMax, BigDecimal overloaded, BigDecimal critical) {
        if (plannedPercent == null) return "UNAVAILABLE";
        if (plannedPercent.compareTo(critical) > 0) return "CRITICAL_OVERLOAD";
        if (plannedPercent.compareTo(overloaded) > 0) return "OVERLOADED";
        if (plannedPercent.compareTo(healthyMax) > 0) return "WATCH";
        if (plannedPercent.compareTo(underAllocated) < 0) return "UNDER_ALLOCATED";
        return "HEALTHY";
    }

    public static BigDecimal availableCapacityHours(int workingDays, BigDecimal hoursPerDay,
                                                    BigDecimal extra, BigDecimal reduced, BigDecimal nonWorking) {
        BigDecimal base = hoursPerDay.multiply(BigDecimal.valueOf(workingDays));
        return base.add(nz(extra)).subtract(nz(reduced)).subtract(nz(nonWorking));
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
