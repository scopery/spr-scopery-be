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
@Table(name = FinanceTableNames.FINANCE_PHASE)
@Getter
@Setter
@NoArgsConstructor
public class PhaseFinanceJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;

    @Column(name = "project_phase_id", nullable = false)
    private UUID projectPhaseId;

    @Column(name = "phase_name_snapshot", nullable = false)
    private String phaseNameSnapshot;

    @Column(name = "phase_order", nullable = false)
    private Integer phaseOrder;

    @Column(name = "estimate_hours", nullable = false, precision = 20, scale = 4)
    private BigDecimal estimateHours;

    @Column(name = "labor_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal laborCost;

    @Column(name = "custom_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal customCost;

    @Column(name = "vendor_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal vendorCost;

    @Column(name = "contingency_amount", nullable = false, precision = 20, scale = 4)
    private BigDecimal contingencyAmount;

    @Column(name = "direct_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal directCost;

    @Column(name = "overhead_amount", nullable = false, precision = 20, scale = 4)
    private BigDecimal overheadAmount;

    @Column(name = "budget_of_costs", nullable = false, precision = 20, scale = 4)
    private BigDecimal budgetOfCosts;

    @Column(name = "planned_revenue", nullable = false, precision = 20, scale = 4)
    private BigDecimal plannedRevenue;

    @Column(name = "revenue_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal revenuePercent;

    @Column(name = "gross_margin", nullable = false, precision = 20, scale = 4)
    private BigDecimal grossMargin;

    @Column(name = "gross_margin_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal grossMarginPercent;

    @Column(name = "profit_before_tax", nullable = false, precision = 20, scale = 4)
    private BigDecimal profitBeforeTax;

    @Column(name = "pbt_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal pbtPercent;

    @Version
    private Integer version;
}
