package com.company.scopery.modules.quote.calculation;

import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Quote calculation engine (QUOTE_V1).
 *
 * CostBase default = budgetOfCosts from frozen finance snapshot (not live finance).
 * Margins:
 *   QuoteGrossMargin = QuoteSubtotal - DirectCost
 *   QuotePBT = QuoteSubtotal - DirectCost - Overhead
 * Tax excluded; taxAmount always null in Phase 18.
 */
@Service
public class QuoteCalculationService {

    public static final int MONEY_SCALE = 4;
    public static final int PERCENT_SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final BigDecimal DISCOUNT_APPROVAL_THRESHOLD_PERCENT = new BigDecimal("10");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final TargetMarginSolver solver;

    public QuoteCalculationService(TargetMarginSolver solver) {
        this.solver = solver;
    }

    public QuoteSummary recalculate(
            QuoteVersion version,
            List<QuoteLine> lines,
            FinanceSnapshotAmounts finance) {
        BigDecimal subtotal = lines.stream()
                .map(QuoteLine::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONEY_SCALE, ROUNDING);

        BigDecimal costBase = resolveCostBase(version, finance);
        BigDecimal directCost = nz(finance.directCost());
        BigDecimal overhead = nz(finance.overhead());

        DiscountResult discount = applyDiscount(version.discountMethod(), version.discountPercent(),
                version.discountAmount(), subtotal);

        BigDecimal afterDiscount = subtotal.subtract(discount.amount()).setScale(MONEY_SCALE, ROUNDING);
        if (afterDiscount.signum() < 0) {
            throw QuoteExceptions.discountInvalid("Discount cannot make subtotal negative");
        }

        BigDecimal total = afterDiscount; // tax excluded
        BigDecimal required = null;
        if (version.targetMarginPercent() != null) {
            required = solver.solveRequiredContractValue(costBase, version.targetMarginPercent());
        }

        BigDecimal grossMargin = total.subtract(directCost).setScale(MONEY_SCALE, ROUNDING);
        BigDecimal pbt = total.subtract(directCost).subtract(overhead).setScale(MONEY_SCALE, ROUNDING);
        BigDecimal grossPct = percentOf(grossMargin, total);
        BigDecimal pbtPct = percentOf(pbt, total);

        return QuoteSummary.create(
                version.id(), version.projectId(), version.currencyCode(),
                costBase, directCost, overhead, subtotal,
                discount.method(), discount.percent(), discount.amount(), afterDiscount,
                total, version.targetMarginPercent(), required,
                grossMargin, grossPct, pbt, pbtPct);
    }

    public BigDecimal resolveCostBase(QuoteVersion version, FinanceSnapshotAmounts finance) {
        return switch (version.costBaseMethod()) {
            case DIRECT_COST -> nz(finance.directCost());
            case CUSTOM -> nz(finance.customCostBase() != null ? finance.customCostBase() : finance.budgetOfCosts());
            case BUDGET_OF_COSTS -> nz(finance.budgetOfCosts());
        };
    }

    public DiscountResult applyDiscount(
            DiscountMethod method,
            BigDecimal discountPercent,
            BigDecimal discountAmount,
            BigDecimal subtotal) {
        DiscountMethod resolved = method == null ? DiscountMethod.NONE : method;
        return switch (resolved) {
            case NONE -> new DiscountResult(DiscountMethod.NONE, null, BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING));
            case FIXED_AMOUNT -> {
                BigDecimal amt = nz(discountAmount).setScale(MONEY_SCALE, ROUNDING);
                if (amt.signum() < 0) {
                    throw QuoteExceptions.discountInvalid("Discount amount must be >= 0");
                }
                yield new DiscountResult(DiscountMethod.FIXED_AMOUNT, discountPercent, amt);
            }
            case PERCENT_OF_SUBTOTAL -> {
                BigDecimal pct = nz(discountPercent);
                if (pct.signum() < 0) {
                    throw QuoteExceptions.discountInvalid("Discount percent must be >= 0");
                }
                BigDecimal amt = subtotal.multiply(pct).divide(HUNDRED, MONEY_SCALE, ROUNDING);
                yield new DiscountResult(DiscountMethod.PERCENT_OF_SUBTOTAL, pct, amt);
            }
        };
    }

    public boolean requiresDiscountApproval(DiscountMethod method, BigDecimal discountPercent,
                                            BigDecimal discountAmount, BigDecimal subtotal) {
        DiscountResult result = applyDiscount(method, discountPercent, discountAmount, subtotal);
        if (result.amount().signum() <= 0 || subtotal.signum() == 0) {
            return false;
        }
        BigDecimal pct = result.percent() != null
                ? result.percent()
                : result.amount().multiply(HUNDRED).divide(subtotal, PERCENT_SCALE, ROUNDING);
        return pct.compareTo(DISCOUNT_APPROVAL_THRESHOLD_PERCENT) > 0;
    }

    public void validateDiscountReason(DiscountMethod method, BigDecimal discountPercent,
                                       BigDecimal discountAmount, BigDecimal subtotal, String reason) {
        DiscountResult result = applyDiscount(method, discountPercent, discountAmount, subtotal);
        if (result.amount().signum() > 0 && (reason == null || reason.isBlank())) {
            throw QuoteExceptions.discountReasonRequired();
        }
    }

    private static BigDecimal percentOf(BigDecimal part, BigDecimal whole) {
        if (whole == null || whole.signum() == 0) {
            return null;
        }
        return part.multiply(HUNDRED).divide(whole, PERCENT_SCALE, ROUNDING);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public record FinanceSnapshotAmounts(
            BigDecimal budgetOfCosts,
            BigDecimal directCost,
            BigDecimal overhead,
            BigDecimal customCostBase
    ) {}

    public record DiscountResult(DiscountMethod method, BigDecimal percent, BigDecimal amount) {}
}
