package com.company.scopery.modules.aiaction.realtime.domain.model;

import com.company.scopery.modules.aiaction.realtime.domain.enums.AiActionEventType;

import java.time.Instant;
import java.util.UUID;

public class AiActionExecutionEvent {

    private final UUID id;
    private final UUID executionId;
    private final long sequence;
    private final int executionVersion;
    private final AiActionEventType eventType;
    private final Instant occurredAt;
    private final String traceId;
    private final String payloadJson;
    private Instant redisPublishedAt;
    private final Instant createdAt;

    private AiActionExecutionEvent(UUID id, UUID executionId, long sequence, int executionVersion,
                                    AiActionEventType eventType, Instant occurredAt, String traceId,
                                    String payloadJson, Instant redisPublishedAt, Instant createdAt) {
        this.id = id;
        this.executionId = executionId;
        this.sequence = sequence;
        this.executionVersion = executionVersion;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.traceId = traceId;
        this.payloadJson = payloadJson;
        this.redisPublishedAt = redisPublishedAt;
        this.createdAt = createdAt;
    }

    public static AiActionExecutionEvent create(UUID executionId, long sequence, int executionVersion,
                                                 AiActionEventType eventType, String traceId, String payloadJson) {
        Instant now = Instant.now();
        return new AiActionExecutionEvent(UUID.randomUUID(), executionId, sequence, executionVersion,
                eventType, now, traceId, payloadJson, null, now);
    }

    public static AiActionExecutionEvent reconstitute(UUID id, UUID executionId, long sequence,
                                                       int executionVersion, AiActionEventType eventType,
                                                       Instant occurredAt, String traceId, String payloadJson,
                                                       Instant redisPublishedAt, Instant createdAt) {
        return new AiActionExecutionEvent(id, executionId, sequence, executionVersion, eventType,
                occurredAt, traceId, payloadJson, redisPublishedAt, createdAt);
    }

    public void markRedisPublished() {
        this.redisPublishedAt = Instant.now();
    }

    public UUID id()                     { return id; }
    public UUID executionId()            { return executionId; }
    public long sequence()               { return sequence; }
    public int executionVersion()        { return executionVersion; }
    public AiActionEventType eventType() { return eventType; }
    public Instant occurredAt()          { return occurredAt; }
    public String traceId()              { return traceId; }
    public String payloadJson()          { return payloadJson; }
    public Instant redisPublishedAt()    { return redisPublishedAt; }
    public Instant createdAt()           { return createdAt; }
}
