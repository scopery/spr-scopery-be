package com.company.scopery.modules.estimation.projectsummary.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.estimation.shared.constant.EstimationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;

@Entity @Table(name = EstimationTableNames.PROJECT_SUMMARY) @Getter @Setter @NoArgsConstructor
public class ProjectEstimateSummaryJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="estimation_run_id", nullable=false, unique=true) private UUID estimationRunId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="total_task_count", nullable=false) private Integer totalTaskCount;
    @Column(name="included_task_count", nullable=false) private Integer includedTaskCount;
    @Column(name="excluded_task_count", nullable=false) private Integer excludedTaskCount;
    @Column(name="unestimated_task_count", nullable=false) private Integer unestimatedTaskCount;
    @Column(name="unresolved_role_task_count", nullable=false) private Integer unresolvedRoleTaskCount;
    @Column(name="unresolved_rate_task_count", nullable=false) private Integer unresolvedRateTaskCount;
    @Column(name="total_estimate_hours", nullable=false) private BigDecimal totalEstimateHours;
    @Column(name="total_labor_cost") private BigDecimal totalLaborCost;
    @Column(name="total_billing_preview") private BigDecimal totalBillingPreview;
    @Column(name="average_cost_rate") private BigDecimal averageCostRate;
    @Column(name="average_billing_rate") private BigDecimal averageBillingRate;
    @Column(name="currency_code") private String currencyCode;
    @Column(name="warning_count", nullable=false) private Integer warningCount;
}
