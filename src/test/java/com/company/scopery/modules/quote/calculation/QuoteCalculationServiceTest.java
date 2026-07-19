package com.company.scopery.modules.quote.calculation;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.quote.quoteline.domain.enums.QuoteLineType;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quotesummary.domain.enums.TaxMode;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quoteversion.domain.enums.CostBaseMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.shared.constant.QuoteFormulaVersions;
import com.company.scopery.modules.quote.shared.error.QuoteErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteCalculationServiceTest {

    private QuoteCalculationService service;
    private final UUID projectId = UUID.randomUUID();
    private final UUID quoteId = UUID.randomUUID();
    private final UUID versionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new QuoteCalculationService(new TargetMarginSolver());
    }

    @Test
    void recalculate_marginAndPbt_fromLinesAndFinanceSnapshot() {
        QuoteVersion version = draftVersion(DiscountMethod.NONE, null, null, null);
        List<QuoteLine> lines = List.of(
                QuoteLine.create(versionId, projectId, null, null, QuoteLineType.PHASE, "P1", null,
                        BigDecimal.ONE, new BigDecimal("1000"), "VND", 0, true, null));
        var finance = new QuoteCalculationService.FinanceSnapshotAmounts(
                new BigDecimal("700"), new BigDecimal("600"), new BigDecimal("100"), null);

        QuoteSummary summary = service.recalculate(version, lines, finance);

        assertThat(summary.subtotalBeforeDiscount()).isEqualByComparingTo("1000");
        assertThat(summary.totalQuotedAmount()).isEqualByComparingTo("1000");
        assertThat(summary.costBase()).isEqualByComparingTo("700");
        assertThat(summary.grossMargin()).isEqualByComparingTo("400"); // 1000 - 600
        assertThat(summary.profitBeforeTax()).isEqualByComparingTo("300"); // 1000 - 600 - 100
        assertThat(summary.taxMode()).isEqualTo(TaxMode.TAX_EXCLUDED);
        assertThat(summary.taxAmount()).isNull();
        assertThat(summary.formulaVersion()).isEqualTo(QuoteFormulaVersions.QUOTE_V1);
    }

    @Test
    void recalculate_percentDiscount_appliesAndRequiresReasonValidationElsewhere() {
        QuoteVersion version = draftVersion(DiscountMethod.PERCENT_OF_SUBTOTAL, new BigDecimal("10"), null, "Promo");
        List<QuoteLine> lines = List.of(
                QuoteLine.create(versionId, projectId, null, null, QuoteLineType.CUSTOM, "Svc", null,
                        BigDecimal.ONE, new BigDecimal("1000"), "VND", 0, true, null));
        var finance = new QuoteCalculationService.FinanceSnapshotAmounts(
                new BigDecimal("500"), new BigDecimal("400"), new BigDecimal("100"), null);

        QuoteSummary summary = service.recalculate(version, lines, finance);

        assertThat(summary.discountAmount()).isEqualByComparingTo("100.0000");
        assertThat(summary.subtotalAfterDiscount()).isEqualByComparingTo("900.0000");
        assertThat(summary.totalQuotedAmount()).isEqualByComparingTo("900.0000");
    }

    @Test
    void recalculate_fixedDiscount_cannotMakeNegative() {
        QuoteVersion version = draftVersion(DiscountMethod.FIXED_AMOUNT, null, new BigDecimal("2000"), "Too much");
        List<QuoteLine> lines = List.of(
                QuoteLine.create(versionId, projectId, null, null, QuoteLineType.CUSTOM, "Svc", null,
                        BigDecimal.ONE, new BigDecimal("1000"), "VND", 0, true, null));
        var finance = new QuoteCalculationService.FinanceSnapshotAmounts(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);

        assertThatThrownBy(() -> service.recalculate(version, lines, finance))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(QuoteErrorCatalog.QUOTE_DISCOUNT_INVALID.code());
    }

    @Test
    void discountAbove10Percent_requiresApproval() {
        boolean required = service.requiresDiscountApproval(
                DiscountMethod.PERCENT_OF_SUBTOTAL, new BigDecimal("10.0001"), null, new BigDecimal("1000"));
        assertThat(required).isTrue();
        boolean notRequired = service.requiresDiscountApproval(
                DiscountMethod.PERCENT_OF_SUBTOTAL, new BigDecimal("10"), null, new BigDecimal("1000"));
        assertThat(notRequired).isFalse();
    }

    @Test
    void validateDiscountReason_requiredWhenDiscountPositive() {
        assertThatThrownBy(() -> service.validateDiscountReason(
                DiscountMethod.FIXED_AMOUNT, null, new BigDecimal("50"), new BigDecimal("1000"), "  "))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(QuoteErrorCatalog.QUOTE_DISCOUNT_REASON_REQUIRED.code());
    }

    private QuoteVersion draftVersion(DiscountMethod discountMethod, BigDecimal discountPercent,
                                      BigDecimal discountAmount, String reason) {
        return new QuoteVersion(
                versionId, quoteId, projectId, UUID.randomUUID(), 1, QuoteVersionStatus.DRAFT,
                "Title", "{}", "{\"summary\":{}}", QuoteFormulaVersions.QUOTE_V1, "VND",
                new BigDecimal("30"), PricingMethod.PHASE_LINE_SUM, CostBaseMethod.BUDGET_OF_COSTS,
                false, discountMethod, discountPercent,
                discountAmount == null ? BigDecimal.ZERO : discountAmount, reason, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                0, null, null);
    }
}
