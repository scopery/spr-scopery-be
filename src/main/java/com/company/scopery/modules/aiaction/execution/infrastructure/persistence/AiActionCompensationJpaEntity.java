package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

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
@Table(name = AiActionTableNames.COMPENSATION)
public class AiActionCompensationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "step_execution_id", nullable = false)
    private UUID stepExecutionId;

    @Column(name = "requested_by", nullable = false)
    private UUID requestedByUserId;

    @Column(name = "tool_code", nullable = false, length = 100)
    private String toolCode;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
