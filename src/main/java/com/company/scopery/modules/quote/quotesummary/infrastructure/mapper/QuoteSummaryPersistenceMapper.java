package com.company.scopery.modules.quote.quotesummary.infrastructure.mapper;

import com.company.scopery.modules.quote.quotesummary.domain.enums.TaxMode;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.infrastructure.persistence.QuoteSummaryJpaEntity;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import org.springframework.stereotype.Component;

@Component
public class QuoteSummaryPersistenceMapper {
    public QuoteSummary toDomain(QuoteSummaryJpaEntity e) {
        return new QuoteSummary(
                e.getId(), e.getQuoteVersionId(), e.getProjectId(), e.getCurrencyCode(),
                e.getCostBase(), e.getDirectCost(), e.getOverhead(), e.getSubtotalBeforeDiscount(),
                DiscountMethod.valueOf(e.getDiscountMethod()), e.getDiscountPercent(), e.getDiscountAmount(),
                e.getSubtotalAfterDiscount(), TaxMode.valueOf(e.getTaxMode()), e.getTaxAmount(),
                e.getTotalQuotedAmount(), e.getTargetMarginPercent(), e.getRequiredContractValue(),
                e.getGrossMargin(), e.getGrossMarginPercent(), e.getProfitBeforeTax(), e.getPbtPercent(),
                e.getFormulaVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public QuoteSummaryJpaEntity toJpaEntity(QuoteSummary d) {
        QuoteSummaryJpaEntity e = new QuoteSummaryJpaEntity();
        e.setId(d.id());
        e.setQuoteVersionId(d.quoteVersionId());
        e.setProjectId(d.projectId());
        e.setCurrencyCode(d.currencyCode());
        e.setCostBase(d.costBase());
        e.setDirectCost(d.directCost());
        e.setOverhead(d.overhead());
        e.setSubtotalBeforeDiscount(d.subtotalBeforeDiscount());
        e.setDiscountMethod(d.discountMethod().name());
        e.setDiscountPercent(d.discountPercent());
        e.setDiscountAmount(d.discountAmount());
        e.setSubtotalAfterDiscount(d.subtotalAfterDiscount());
        e.setTaxMode(d.taxMode().name());
        e.setTaxAmount(d.taxAmount());
        e.setTotalQuotedAmount(d.totalQuotedAmount());
        e.setTargetMarginPercent(d.targetMarginPercent());
        e.setRequiredContractValue(d.requiredContractValue());
        e.setGrossMargin(d.grossMargin());
        e.setGrossMarginPercent(d.grossMarginPercent());
        e.setProfitBeforeTax(d.profitBeforeTax());
        e.setPbtPercent(d.pbtPercent());
        e.setFormulaVersion(d.formulaVersion());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
