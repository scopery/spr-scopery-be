package com.company.scopery.modules.aiaction.realtime.domain.model;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionControlCommandStatus;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionControlCommandType;

import java.time.Instant;
import java.util.UUID;

public class AiActionControlCommand {

    private final UUID id;
    private final UUID executionId;
    private final AiActionControlCommandType commandType;
    private final UUID issuedByUserId;
    private final int expectedExecutionVersion;
    private final String idempotencyKey;
    private AiActionControlCommandStatus status;
    private final Instant createdAt;
    private Instant processedAt;

    private AiActionControlCommand(UUID id, UUID executionId, AiActionControlCommandType commandType,
                                    UUID issuedByUserId, int expectedExecutionVersion,
                                    String idempotencyKey, AiActionControlCommandStatus status,
                                    Instant createdAt, Instant processedAt) {
        this.id = id;
        this.executionId = executionId;
        this.commandType = commandType;
        this.issuedByUserId = issuedByUserId;
        this.expectedExecutionVersion = expectedExecutionVersion;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public static AiActionControlCommand create(UUID executionId, AiActionControlCommandType commandType,
                                                 UUID issuedByUserId, int expectedExecutionVersion,
                                                 String idempotencyKey) {
        return new AiActionControlCommand(UUID.randomUUID(), executionId, commandType, issuedByUserId,
                expectedExecutionVersion, idempotencyKey, AiActionControlCommandStatus.ACCEPTED,
                Instant.now(), null);
    }

    public static AiActionControlCommand reconstitute(UUID id, UUID executionId,
                                                       AiActionControlCommandType commandType,
                                                       UUID issuedByUserId, int expectedExecutionVersion,
                                                       String idempotencyKey, AiActionControlCommandStatus status,
                                                       Instant createdAt, Instant processedAt) {
        return new AiActionControlCommand(id, executionId, commandType, issuedByUserId,
                expectedExecutionVersion, idempotencyKey, status, createdAt, processedAt);
    }

    public void markApplied() {
        this.status = AiActionControlCommandStatus.APPLIED;
        this.processedAt = Instant.now();
    }

    public void markRejected() {
        this.status = AiActionControlCommandStatus.REJECTED;
        this.processedAt = Instant.now();
    }

    public UUID id()                                    { return id; }
    public UUID executionId()                           { return executionId; }
    public AiActionControlCommandType commandType()     { return commandType; }
    public UUID issuedByUserId()                        { return issuedByUserId; }
    public int expectedExecutionVersion()               { return expectedExecutionVersion; }
    public String idempotencyKey()                      { return idempotencyKey; }
    public AiActionControlCommandStatus status()        { return status; }
    public Instant createdAt()                          { return createdAt; }
    public Instant processedAt()                        { return processedAt; }
}
