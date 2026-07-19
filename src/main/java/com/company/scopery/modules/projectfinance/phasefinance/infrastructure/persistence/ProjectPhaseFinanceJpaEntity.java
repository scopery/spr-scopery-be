package com.company.scopery.modules.projectfinance.phasefinance.infrastructure.persistence;

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
@Table(name = ProjectFinanceTableNames.PHASE_FINANCE)
@Getter
@Setter
@NoArgsConstructor
public class ProjectPhaseFinanceJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "project_phase_id", nullable = false)
    private UUID projectPhaseId;
    @Column(name = "phase_name_snapshot", nullable = false)
    private String phaseNameSnapshot;
    @Column(name = "phase_order")
    private Integer phaseOrder;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "estimate_hours", nullable = false)
    private BigDecimal estimateHours;
    @Column(name = "labor_cost", nullable = false)
    private BigDecimal laborCost;
    @Column(name = "custom_cost", nullable = false)
    private BigDecimal customCost;
    @Column(name = "vendor_cost", nullable = false)
    private BigDecimal vendorCost;
    @Column(name = "contingency_amount", nullable = false)
    private BigDecimal contingencyAmount;
    @Column(name = "direct_cost", nullable = false)
    private BigDecimal directCost;
    @Column(name = "overhead_amount", nullable = false)
    private BigDecimal overheadAmount;
    @Column(name = "budget_of_costs", nullable = false)
    private BigDecimal budgetOfCosts;
    @Column(name = "planned_revenue", nullable = false)
    private BigDecimal plannedRevenue;
    @Column(name = "revenue_percent")
    private BigDecimal revenuePercent;
    @Column(name = "gross_margin")
    private BigDecimal grossMargin;
    @Column(name = "gross_margin_percent")
    private BigDecimal grossMarginPercent;
    @Column(name = "profit_before_tax")
    private BigDecimal profitBeforeTax;
    @Column(name = "pbt_percent")
    private BigDecimal pbtPercent;
}
