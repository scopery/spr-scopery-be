package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.EXECUTION)
public class AiActionExecutionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false, unique = true)
    private UUID planId;

    @Column(name = "initiated_by", nullable = false)
    private UUID initiatedByUserId;

    @Column(name = "execution_key", nullable = false, unique = true, length = 200)
    private String executionKey;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "execution_version", nullable = false)
    private int executionVersion;

    @Column(name = "worker_instance_id", length = 100)
    private String workerInstanceId;

    @Column(name = "lease_expires_at")
    private Instant leaseExpiresAt;

    @Column(name = "current_step_ordinal")
    private Integer currentStepOrdinal;

    @Column(name = "succeeded_count", nullable = false)
    private int succeededCount;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "skipped_count", nullable = false)
    private int skippedCount;

    @Column(name = "compensated_count", nullable = false)
    private int compensatedCount;

    @Column(name = "cancelled_count", nullable = false)
    private int cancelledCount;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Override
    public Object getId() {
        return id;
    }
}
