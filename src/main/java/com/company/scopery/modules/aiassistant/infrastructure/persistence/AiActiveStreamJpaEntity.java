package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
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
@Table(
    name = AiAssistantTableNames.ACTIVE_STREAM,
    indexes = {
        @Index(name = "idx_aiassistant_active_stream_message_id", columnList = "message_id"),
        @Index(name = "idx_aiassistant_active_stream_workspace_id", columnList = "workspace_id"),
        @Index(name = "idx_aiassistant_active_stream_stream_status", columnList = "stream_status"),
        @Index(name = "idx_aiassistant_active_stream_expires_at", columnList = "expires_at")
    }
)
public class AiActiveStreamJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "actor_id", nullable = false, updatable = false)
    private UUID actorId;

    @Column(name = "stream_status", nullable = false, length = 50)
    private String streamStatus;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private Instant acquiredAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "released_at")
    private Instant releasedAt;
}
