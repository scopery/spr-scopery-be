package com.company.scopery.modules.quote.quotesummary.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quote.shared.constant.QuoteTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = QuoteTableNames.SUMMARY)
@Getter
@Setter
@NoArgsConstructor
public class QuoteSummaryJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "quote_version_id", nullable = false)
    private UUID quoteVersionId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "cost_base", nullable = false)
    private BigDecimal costBase;
    @Column(name = "direct_cost", nullable = false)
    private BigDecimal directCost;
    @Column(nullable = false)
    private BigDecimal overhead;
    @Column(name = "subtotal_before_discount", nullable = false)
    private BigDecimal subtotalBeforeDiscount;
    @Column(name = "discount_method", nullable = false)
    private String discountMethod;
    @Column(name = "discount_percent")
    private BigDecimal discountPercent;
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
    @Column(name = "subtotal_after_discount", nullable = false)
    private BigDecimal subtotalAfterDiscount;
    @Column(name = "tax_mode", nullable = false)
    private String taxMode;
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;
    @Column(name = "total_quoted_amount", nullable = false)
    private BigDecimal totalQuotedAmount;
    @Column(name = "target_margin_percent")
    private BigDecimal targetMarginPercent;
    @Column(name = "required_contract_value")
    private BigDecimal requiredContractValue;
    @Column(name = "gross_margin")
    private BigDecimal grossMargin;
    @Column(name = "gross_margin_percent")
    private BigDecimal grossMarginPercent;
    @Column(name = "profit_before_tax")
    private BigDecimal profitBeforeTax;
    @Column(name = "pbt_percent")
    private BigDecimal pbtPercent;
    @Column(name = "formula_version", nullable = false)
    private String formulaVersion;
}
