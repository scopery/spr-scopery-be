package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAssistantTableNames.QUOTA_USAGE,
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_aiassistant_quota_usage_workspace_actor_date",
                columnNames = {"workspace_id", "actor_id", "usage_date"})
    },
    indexes = {
        @Index(name = "idx_aiassistant_quota_usage_workspace_id", columnList = "workspace_id"),
        @Index(name = "idx_aiassistant_quota_usage_actor_id", columnList = "actor_id"),
        @Index(name = "idx_aiassistant_quota_usage_usage_date", columnList = "usage_date")
    }
)
public class AiQuotaUsageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "actor_id", nullable = false, updatable = false)
    private UUID actorId;

    @Column(name = "usage_date", nullable = false, updatable = false)
    private LocalDate usageDate;

    @Column(name = "turn_count", nullable = false)
    private int turnCount;

    @Column(name = "input_token_count", nullable = false)
    private long inputTokenCount;

    @Column(name = "output_token_count", nullable = false)
    private long outputTokenCount;

    @Column(name = "failed_turn_count", nullable = false)
    private int failedTurnCount;

    @Column(name = "blocked_turn_count", nullable = false)
    private int blockedTurnCount;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
