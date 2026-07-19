package com.company.scopery.modules.projectfinance.scenario.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceTableNames;
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
@Table(name = ProjectFinanceTableNames.SCENARIO)
@Getter
@Setter
@NoArgsConstructor
public class ProjectFinanceScenarioJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "estimation_run_id", nullable = false)
    private UUID estimationRunId;
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "scenario_version", nullable = false)
    private Integer scenarioVersion;
    @Column(nullable = false)
    private String status;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "planned_revenue", nullable = false)
    private BigDecimal plannedRevenue;
    @Column(name = "revenue_split_method", nullable = false)
    private String revenueSplitMethod;
    @Column(name = "contingency_method")
    private String contingencyMethod;
    @Column(name = "contingency_percent")
    private BigDecimal contingencyPercent;
    @Column(name = "contingency_fixed_amount")
    private BigDecimal contingencyFixedAmount;
    @Column(name = "overhead_method")
    private String overheadMethod;
    @Column(name = "overhead_percent")
    private BigDecimal overheadPercent;
    @Column(name = "overhead_fixed_amount")
    private BigDecimal overheadFixedAmount;
    @Column(name = "target_margin_percent")
    private BigDecimal targetMarginPercent;
    @Column(name = "assumptions_json", columnDefinition = "jsonb")
    private String assumptionsJson;
    @Column(name = "formula_version", nullable = false)
    private String formulaVersion;
    @Column(name = "current_flag", nullable = false)
    private Boolean currentFlag;
    @Column(name = "approved_at")
    private Instant approvedAt;
    @Column(name = "approved_by")
    private UUID approvedBy;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "archived_by")
    private UUID archivedBy;
    @Column(name = "actor_user_id")
    private UUID actorUserId;
    @Column(name = "trace_id")
    private String traceId;
    @Version
    private Integer version;
}
