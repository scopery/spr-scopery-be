package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.CONTROL_COMMAND)
public class AiActionControlCommandJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "command_type", nullable = false, length = 20)
    private String commandType;

    @Column(name = "issued_by", nullable = false)
    private UUID issuedByUserId;

    @Column(name = "expected_execution_version", nullable = false)
    private int expectedExecutionVersion;

    @Column(name = "idempotency_key", nullable = false, length = 300)
    private String idempotencyKey;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;
}
