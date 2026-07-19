package com.company.scopery.modules.estimation.tasksnapshot.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.estimation.shared.constant.EstimationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = EstimationTableNames.TASK_SNAPSHOT)
@Getter @Setter @NoArgsConstructor
public class TaskEstimateSnapshotJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "estimation_run_id", nullable = false) private UUID estimationRunId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "project_phase_id") private UUID projectPhaseId;
    @Column(name = "wbs_node_id") private UUID wbsNodeId;
    @Column(name = "task_id", nullable = false) private UUID taskId;
    @Column(name = "task_code") private String taskCode;
    @Column(name = "task_title", nullable = false) private String taskTitle;
    @Column(name = "assignee_user_id") private UUID assigneeUserId;
    @Column(name = "workspace_member_id") private UUID workspaceMemberId;
    @Column(name = "cost_role_id") private UUID costRoleId;
    @Column(name = "cost_role_code") private String costRoleCode;
    @Column(name = "estimate_hours", nullable = false) private BigDecimal estimateHours;
    @Column(name = "rate_target_date", nullable = false) private LocalDate rateTargetDate;
    @Column(name = "rate_card_id") private UUID rateCardId;
    @Column(name = "rate_card_version_id") private UUID rateCardVersionId;
    @Column(name = "rate_card_line_id") private UUID rateCardLineId;
    @Column(name = "base_cost_rate") private BigDecimal baseCostRate;
    @Column(name = "adjusted_cost_rate") private BigDecimal adjustedCostRate;
    @Column(name = "base_billing_rate") private BigDecimal baseBillingRate;
    @Column(name = "adjusted_billing_rate") private BigDecimal adjustedBillingRate;
    @Column(name = "currency_code") private String currencyCode;
    @Column(name = "inflation_policy_id") private UUID inflationPolicyId;
    @Column(name = "inflation_percent") private BigDecimal inflationPercent;
    @Column(name = "years_forward") private BigDecimal yearsForward;
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "estimated_labor_cost") private BigDecimal estimatedLaborCost;
    @Column(name = "estimated_billing_preview") private BigDecimal estimatedBillingPreview;
    @Column(nullable = false) private String status;
    @Column(name = "issue_code") private String issueCode;
    @Column(name = "issue_message") private String issueMessage;
}
