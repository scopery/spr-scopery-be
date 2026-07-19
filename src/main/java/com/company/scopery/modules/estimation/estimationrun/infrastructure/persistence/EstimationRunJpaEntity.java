package com.company.scopery.modules.estimation.estimationrun.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.estimation.shared.constant.EstimationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = EstimationTableNames.ESTIMATION_RUN)
@Getter
@Setter
@NoArgsConstructor
public class EstimationRunJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "schedule_run_id")
    private UUID scheduleRunId;
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private String status;
    @Column(name = "calculation_mode", nullable = false)
    private String calculationMode;
    @Column(name = "rate_target_date_strategy", nullable = false)
    private String rateTargetDateStrategy;
    @Column(name = "currency_policy", nullable = false)
    private String currencyPolicy;
    @Column(name = "assumptions_json", columnDefinition = "jsonb")
    private String assumptionsJson;
    @Column(name = "result_summary_json", columnDefinition = "jsonb")
    private String resultSummaryJson;
    @Column(name = "error_code")
    private String errorCode;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "completed_at")
    private Instant completedAt;
    @Column(name = "actor_user_id")
    private UUID actorUserId;
    @Column(name = "trace_id")
    private String traceId;
    @Version
    private Integer version;
}
