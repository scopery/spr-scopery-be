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
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = FinanceTableNames.FINANCE_SCENARIO)
@Getter
@Setter
@NoArgsConstructor
public class FinanceScenarioJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "estimation_run_id")
    private UUID estimationRunId;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "scenario_version", nullable = false)
    private Integer scenarioVersion;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(name = "planned_revenue", nullable = false, precision = 20, scale = 4)
    private BigDecimal plannedRevenue;

    @Column(name = "revenue_split_method", length = 50)
    private String revenueSplitMethod;

    @Column(name = "contingency_method", nullable = false, length = 50)
    private String contingencyMethod;

    @Column(name = "contingency_percent", precision = 10, scale = 4)
    private BigDecimal contingencyPercent;

    @Column(name = "contingency_fixed_amount", precision = 20, scale = 4)
    private BigDecimal contingencyFixedAmount;

    @Column(name = "overhead_method", nullable = false, length = 50)
    private String overheadMethod;

    @Column(name = "overhead_percent", precision = 10, scale = 4)
    private BigDecimal overheadPercent;

    @Column(name = "overhead_fixed_amount", precision = 20, scale = 4)
    private BigDecimal overheadFixedAmount;

    @Column(name = "target_margin_percent", precision = 10, scale = 4)
    private BigDecimal targetMarginPercent;

    @Column(name = "assumptions_json", columnDefinition = "text")
    private String assumptionsJson;

    @Column(name = "current_flag", nullable = false)
    private boolean currentFlag;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "formula_version", length = 50)
    private String formulaVersion;

    @Version
    private Integer version;
}
