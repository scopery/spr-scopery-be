package com.company.scopery.modules.estimation.wbsrollup.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.estimation.shared.constant.EstimationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;

@Entity @Table(name = EstimationTableNames.WBS_ROLLUP) @Getter @Setter @NoArgsConstructor
public class WbsEstimateRollupJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="estimation_run_id", nullable=false) private UUID estimationRunId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="wbs_node_id", nullable=false) private UUID wbsNodeId;
    @Column(name="parent_wbs_node_id") private UUID parentWbsNodeId;
    @Column(nullable=false) private Integer depth;
    @Column(name="task_count", nullable=false) private Integer taskCount;
    @Column(name="included_task_count", nullable=false) private Integer includedTaskCount;
    @Column(name="unresolved_task_count", nullable=false) private Integer unresolvedTaskCount;
    @Column(name="total_estimate_hours", nullable=false) private BigDecimal totalEstimateHours;
    @Column(name="total_labor_cost") private BigDecimal totalLaborCost;
    @Column(name="total_billing_preview") private BigDecimal totalBillingPreview;
    @Column(name="currency_code") private String currencyCode;
}
