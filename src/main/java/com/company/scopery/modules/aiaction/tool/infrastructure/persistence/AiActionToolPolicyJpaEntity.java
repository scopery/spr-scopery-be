package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

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
@Table(name = AiActionTableNames.TOOL_POLICY)
public class AiActionToolPolicyJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tool_code", nullable = false, length = 100)
    private String toolCode;

    @Column(name = "tool_version", nullable = false, length = 20)
    private String toolVersion;

    @Column(name = "invocation_scope", nullable = false, length = 40)
    private String invocationScope;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "execution_mode", nullable = false, length = 40)
    private String executionMode;

    @Column(name = "max_batch_targets", nullable = false)
    private int maxBatchTargets;

    @Column(name = "dry_run_required", nullable = false)
    private boolean dryRunRequired;

    @Column(name = "supports_compensation", nullable = false)
    private boolean supportsCompensation;

    @Column(name = "supports_pause", nullable = false)
    private boolean supportsPause;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
