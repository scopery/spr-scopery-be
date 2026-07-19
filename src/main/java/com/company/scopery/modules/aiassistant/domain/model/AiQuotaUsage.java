package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class AiQuotaUsage {

    private final UUID id;
    private final UUID workspaceId;
    private final UUID actorId;
    private final LocalDate usageDate;
    private int turnCount;
    private long inputTokenCount;
    private long outputTokenCount;
    private int failedTurnCount;
    private int blockedTurnCount;
    private Instant updatedAt;
    private long version;

    private AiQuotaUsage(UUID id, UUID workspaceId, UUID actorId, LocalDate usageDate,
                         int turnCount, long inputTokenCount, long outputTokenCount,
                         int failedTurnCount, int blockedTurnCount, Instant updatedAt, long version) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.actorId = actorId;
        this.usageDate = usageDate;
        this.turnCount = turnCount;
        this.inputTokenCount = inputTokenCount;
        this.outputTokenCount = outputTokenCount;
        this.failedTurnCount = failedTurnCount;
        this.blockedTurnCount = blockedTurnCount;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static AiQuotaUsage createForToday(UUID workspaceId, UUID actorId) {
        return new AiQuotaUsage(UUID.randomUUID(), workspaceId, actorId, LocalDate.now(),
                0, 0L, 0L, 0, 0, Instant.now(), 0L);
    }

    public static AiQuotaUsage reconstitute(UUID id, UUID workspaceId, UUID actorId, LocalDate usageDate,
                                             int turnCount, long inputTokenCount, long outputTokenCount,
                                             int failedTurnCount, int blockedTurnCount,
                                             Instant updatedAt, long version) {
        return new AiQuotaUsage(id, workspaceId, actorId, usageDate, turnCount, inputTokenCount,
                outputTokenCount, failedTurnCount, blockedTurnCount, updatedAt, version);
    }

    public void incrementTurn() {
        this.turnCount++;
        this.updatedAt = Instant.now();
    }

    public void addTokens(long inputTokens, long outputTokens) {
        this.inputTokenCount += inputTokens;
        this.outputTokenCount += outputTokens;
        this.updatedAt = Instant.now();
    }

    public void incrementFailedTurn() {
        this.failedTurnCount++;
        this.updatedAt = Instant.now();
    }

    public void incrementBlockedTurn() {
        this.blockedTurnCount++;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID workspaceId() { return workspaceId; }
    public UUID actorId() { return actorId; }
    public LocalDate usageDate() { return usageDate; }
    public int turnCount() { return turnCount; }
    public long inputTokenCount() { return inputTokenCount; }
    public long outputTokenCount() { return outputTokenCount; }
    public int failedTurnCount() { return failedTurnCount; }
    public int blockedTurnCount() { return blockedTurnCount; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
