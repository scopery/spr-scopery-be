package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ActiveStreamStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActiveStream {

    private final UUID id;
    private final UUID messageId;
    private final UUID workspaceId;
    private final UUID actorId;
    private ActiveStreamStatus streamStatus;
    private final Instant acquiredAt;
    private final Instant expiresAt;
    private Instant releasedAt;

    private AiActiveStream(UUID id, UUID messageId, UUID workspaceId, UUID actorId,
                           ActiveStreamStatus streamStatus, Instant acquiredAt,
                           Instant expiresAt, Instant releasedAt) {
        this.id = id;
        this.messageId = messageId;
        this.workspaceId = workspaceId;
        this.actorId = actorId;
        this.streamStatus = streamStatus;
        this.acquiredAt = acquiredAt;
        this.expiresAt = expiresAt;
        this.releasedAt = releasedAt;
    }

    public static AiActiveStream acquire(UUID messageId, UUID workspaceId, UUID actorId, Instant expiresAt) {
        Instant now = Instant.now();
        return new AiActiveStream(UUID.randomUUID(), messageId, workspaceId, actorId,
                ActiveStreamStatus.ACTIVE, now, expiresAt, null);
    }

    public static AiActiveStream reconstitute(UUID id, UUID messageId, UUID workspaceId, UUID actorId,
                                               ActiveStreamStatus streamStatus, Instant acquiredAt,
                                               Instant expiresAt, Instant releasedAt) {
        return new AiActiveStream(id, messageId, workspaceId, actorId, streamStatus, acquiredAt, expiresAt, releasedAt);
    }

    public void release() {
        this.streamStatus = ActiveStreamStatus.RELEASED;
        this.releasedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID messageId() { return messageId; }
    public UUID workspaceId() { return workspaceId; }
    public UUID actorId() { return actorId; }
    public ActiveStreamStatus streamStatus() { return streamStatus; }
    public Instant acquiredAt() { return acquiredAt; }
    public Instant expiresAt() { return expiresAt; }
    public Instant releasedAt() { return releasedAt; }
}
