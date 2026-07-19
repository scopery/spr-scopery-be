package com.company.scopery.modules.projectfinance.summary.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceTableNames;
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
@Table(name = ProjectFinanceTableNames.SUMMARY)
@Getter
@Setter
@NoArgsConstructor
public class ProjectFinanceSummaryJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "total_estimate_hours", nullable = false)
    private BigDecimal totalEstimateHours;
    @Column(name = "total_labor_cost", nullable = false)
    private BigDecimal totalLaborCost;
    @Column(name = "total_custom_cost", nullable = false)
    private BigDecimal totalCustomCost;
    @Column(name = "total_vendor_cost", nullable = false)
    private BigDecimal totalVendorCost;
    @Column(name = "total_contingency", nullable = false)
    private BigDecimal totalContingency;
    @Column(name = "total_direct_cost", nullable = false)
    private BigDecimal totalDirectCost;
    @Column(name = "total_overhead", nullable = false)
    private BigDecimal totalOverhead;
    @Column(name = "budget_of_costs", nullable = false)
    private BigDecimal budgetOfCosts;
    @Column(name = "planned_revenue", nullable = false)
    private BigDecimal plannedRevenue;
    @Column(name = "gross_margin")
    private BigDecimal grossMargin;
    @Column(name = "gross_margin_percent")
    private BigDecimal grossMarginPercent;
    @Column(name = "profit_before_tax")
    private BigDecimal profitBeforeTax;
    @Column(name = "pbt_percent")
    private BigDecimal pbtPercent;
    @Column(name = "average_cost_rate")
    private BigDecimal averageCostRate;
    @Column(name = "formula_version", nullable = false)
    private String formulaVersion;
}
