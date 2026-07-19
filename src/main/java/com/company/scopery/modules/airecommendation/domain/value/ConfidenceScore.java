package com.company.scopery.modules.airecommendation.domain.value;

import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceLabel;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;

import java.math.BigDecimal;

public record ConfidenceScore(ConfidenceMethod method, BigDecimal value, ConfidenceLabel label) {

    private static final BigDecimal HIGH_THRESHOLD   = new BigDecimal("0.8500");
    private static final BigDecimal MEDIUM_THRESHOLD = new BigDecimal("0.6500");
    private static final BigDecimal LOW_THRESHOLD    = new BigDecimal("0.4000");

    public static ConfidenceScore of(ConfidenceMethod method, BigDecimal value) {
        return new ConfidenceScore(method, value, deriveLabel(value));
    }

    public boolean isAboveMinimum() {
        return value.compareTo(LOW_THRESHOLD) >= 0;
    }

    private static ConfidenceLabel deriveLabel(BigDecimal value) {
        if (value.compareTo(HIGH_THRESHOLD) >= 0) return ConfidenceLabel.HIGH;
        if (value.compareTo(MEDIUM_THRESHOLD) >= 0) return ConfidenceLabel.MEDIUM;
        return ConfidenceLabel.LOW;
    }
}
