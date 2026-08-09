package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.finance.shared.constant.FinanceTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = FinanceTableNames.FINANCE_SUMMARY)
@Getter
@Setter
@NoArgsConstructor
public class FinanceSummaryJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(name = "total_estimate_hours", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalEstimateHours;

    @Column(name = "total_labor_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalLaborCost;

    @Column(name = "total_custom_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalCustomCost;

    @Column(name = "total_vendor_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalVendorCost;

    @Column(name = "total_contingency", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalContingency;

    @Column(name = "total_direct_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalDirectCost;

    @Column(name = "total_overhead", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalOverhead;

    @Column(name = "budget_of_costs", nullable = false, precision = 20, scale = 4)
    private BigDecimal budgetOfCosts;

    @Column(name = "planned_revenue", nullable = false, precision = 20, scale = 4)
    private BigDecimal plannedRevenue;

    @Column(name = "gross_margin", nullable = false, precision = 20, scale = 4)
    private BigDecimal grossMargin;

    @Column(name = "gross_margin_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal grossMarginPercent;

    @Column(name = "profit_before_tax", nullable = false, precision = 20, scale = 4)
    private BigDecimal profitBeforeTax;

    @Column(name = "pbt_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal pbtPercent;

    @Column(name = "average_cost_rate", precision = 20, scale = 4)
    private BigDecimal averageCostRate;

    @Column(name = "formula_version", length = 50)
    private String formulaVersion;

    @Version
    private Integer version;
}
