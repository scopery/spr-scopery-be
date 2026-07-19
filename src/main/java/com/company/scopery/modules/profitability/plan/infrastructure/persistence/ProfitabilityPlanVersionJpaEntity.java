package com.company.scopery.modules.profitability.plan.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.PLAN_VERSION)
@Getter
@Setter
@NoArgsConstructor
public class ProfitabilityPlanVersionJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "profitability_plan_id", nullable = false)
    private UUID profitabilityPlanId;
    @Column(name = "version_number", nullable = false)
    private int versionNumber;
    @Column(name = "version_label", length = 100)
    private String versionLabel;
    @Column(nullable = false, length = 10)
    private String currency;
    @Column(name = "baseline_revenue", nullable = false, precision = 19, scale = 4)
    private BigDecimal baselineRevenue;
    @Column(name = "baseline_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal baselineCost;
    @Column(name = "baseline_profit", nullable = false, precision = 19, scale = 4)
    private BigDecimal baselineProfit;
    @Column(name = "baseline_margin_percent", precision = 9, scale = 4)
    private BigDecimal baselineMarginPercent;
    @Column(name = "planned_revenue", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedRevenue;
    @Column(name = "planned_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedCost;
    @Column(name = "planned_profit", nullable = false, precision = 19, scale = 4)
    private BigDecimal plannedProfit;
    @Column(name = "planned_margin_percent", precision = 9, scale = 4)
    private BigDecimal plannedMarginPercent;
    @Column(name = "assumption_notes", columnDefinition = "TEXT")
    private String assumptionNotes;
    @Column(name = "source_quote_version_id")
    private UUID sourceQuoteVersionId;
    @Column(name = "source_baseline_id")
    private UUID sourceBaselineId;
    @Column(name = "finalized_flag", nullable = false)
    private boolean finalizedFlag;
    @Column(name = "finalized_at")
    private Instant finalizedAt;
    @Column(name = "finalized_by")
    private UUID finalizedBy;
    @Column(nullable = false, length = 50)
    private String status;
    @Version
    private Integer version;
}
