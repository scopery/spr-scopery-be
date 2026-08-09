package com.company.scopery.modules.aiplanning.planningrun.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = AiPlanningTableNames.PLANNING_RUN)
@Getter
@Setter
@NoArgsConstructor
public class AiPlanningRunJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "actor_user_id", nullable = false)
    private UUID actorUserId;
    @Column(name = "agent_id")
    private UUID agentId;
    @Column(name = "agent_version_id")
    private UUID agentVersionId;
    @Column(name = "prompt_template_id")
    private UUID promptTemplateId;
    @Column(name = "prompt_template_version_id")
    private UUID promptTemplateVersionId;
    @Column(name = "model_deployment_id")
    private UUID modelDeploymentId;
    @Column(name = "ai_execution_log_id")
    private UUID aiExecutionLogId;
    @Column(name = "run_type", nullable = false)
    private String runType;
    @Column(nullable = false)
    private String status;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_summary_json", columnDefinition = "jsonb")
    private String inputSummaryJson;
    @Column(name = "context_snapshot_id")
    private UUID contextSnapshotId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "output_summary_json", columnDefinition = "jsonb")
    private String outputSummaryJson;
    @Column(name = "error_code")
    private String errorCode;
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
    @Column(name = "started_at")
    private Instant startedAt;
    @Column(name = "completed_at")
    private Instant completedAt;
    @Column(name = "trace_id")
    private String traceId;
    @Version
    private Integer version;

    /**
     * Override isNew() to use @Version as the new-entity signal.
     * AiPlanningRun.create() sets createdAt=Instant.now() (non-null), so the inherited
     * createdAt-null check from AuditableJpaEntity always returns false, causing Spring Data
     * to call merge() instead of persist() for new entities, which triggers a stale-version
     * OptimisticLockException. Using version==null is the correct indicator here because
     * Hibernate only sets @Version after the first INSERT.
     */
    @Override
    public boolean isNew() {
        return version == null;
    }

    // NOTE: getId() is intentionally NOT overridden here.
    // Lombok @Getter generates: public UUID getId() { return id; }
    // which satisfies the Persistable<Object> contract via covariant return type.
    // An explicit Object-typed override would break the mapper's type inference.
}
