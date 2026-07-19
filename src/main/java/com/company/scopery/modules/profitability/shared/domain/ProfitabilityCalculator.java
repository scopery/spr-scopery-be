package com.company.scopery.modules.profitability.shared.domain;
import com.company.scopery.modules.profitability.profile.domain.enums.ProfitabilityStatus;
import java.math.BigDecimal; import java.math.RoundingMode;
public final class ProfitabilityCalculator {
    private ProfitabilityCalculator(){}
    public static BigDecimal profit(BigDecimal revenue, BigDecimal cost) {
        return nullToZero(revenue).subtract(nullToZero(cost));
    }
    public static BigDecimal marginPercent(BigDecimal revenue, BigDecimal cost) {
        BigDecimal rev = nullToZero(revenue);
        if (rev.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return profit(rev, cost).multiply(BigDecimal.valueOf(100)).divide(rev, 4, RoundingMode.HALF_UP);
    }
    public static ProfitabilityStatus status(BigDecimal marginPercent, BigDecimal healthy, BigDecimal watch, BigDecimal atRisk, BigDecimal lossRisk) {
        BigDecimal m = nullToZero(marginPercent);
        if (m.compareTo(nullToZero(healthy)) >= 0) return ProfitabilityStatus.HEALTHY;
        if (m.compareTo(nullToZero(watch)) >= 0) return ProfitabilityStatus.WATCH;
        if (m.compareTo(nullToZero(atRisk)) >= 0) return ProfitabilityStatus.AT_RISK;
        if (m.compareTo(nullToZero(lossRisk)) >= 0) return ProfitabilityStatus.LOSS_RISK;
        return ProfitabilityStatus.LOSS;
    }
    private static BigDecimal nullToZero(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
