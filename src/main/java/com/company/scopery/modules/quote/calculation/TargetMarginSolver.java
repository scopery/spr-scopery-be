package com.company.scopery.modules.quote.calculation;

import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Target margin solver for Phase 18.
 *
 * TargetMarginPercent is accepted on the 0–100 scale (same as finance target_margin_percent).
 * Example: API value 30.0 means 30% → ratio 0.30.
 *
 * RequiredContractValue = CostBase / (1 - TargetMarginRatio)
 */
@Component
public class TargetMarginSolver {

    public static final int MONEY_SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal ONE = BigDecimal.ONE;

    public BigDecimal solveRequiredContractValue(BigDecimal costBase, BigDecimal targetMarginPercent) {
        BigDecimal base = costBase == null ? BigDecimal.ZERO : costBase;
        if (base.signum() < 0) {
            throw QuoteExceptions.solverInvalidCostBase();
        }
        if (targetMarginPercent == null) {
            throw QuoteExceptions.solverInvalidTargetMargin();
        }
        if (targetMarginPercent.compareTo(HUNDRED) >= 0 || targetMarginPercent.signum() < 0) {
            throw QuoteExceptions.solverInvalidTargetMargin();
        }
        if (targetMarginPercent.signum() == 0) {
            return base.setScale(MONEY_SCALE, ROUNDING);
        }
        BigDecimal ratio = toRatio(targetMarginPercent);
        BigDecimal denominator = ONE.subtract(ratio);
        if (denominator.signum() <= 0) {
            throw QuoteExceptions.solverInvalidTargetMargin();
        }
        try {
            return base.divide(denominator, MONEY_SCALE, ROUNDING);
        } catch (ArithmeticException ex) {
            throw QuoteExceptions.solverFailed("Unable to compute required contract value");
        }
    }

    /**
     * Converts 0–100 scale percent to 0–1 ratio. Values already &lt; 1.0 are treated as ratios
     * only when strictly less than 1 and the API contract documents 0–100; Phase 18 always
     * treats input as 0–100 scale like finance.
     */
    public BigDecimal toRatio(BigDecimal targetMarginPercent) {
        return targetMarginPercent.divide(HUNDRED, MONEY_SCALE + 4, ROUNDING);
    }
}
