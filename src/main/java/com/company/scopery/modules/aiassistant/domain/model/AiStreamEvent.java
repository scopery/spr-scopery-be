package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiStreamEvent {

    private final UUID id;
    private final UUID messageId;
    private final long sequence;
    private final String eventType;
    private final String payloadJson;
    private final String payloadHash;
    private final Instant createdAt;
    private final Instant expiresAt;

    private AiStreamEvent(UUID id, UUID messageId, long sequence, String eventType,
                          String payloadJson, String payloadHash, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.messageId = messageId;
        this.sequence = sequence;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.payloadHash = payloadHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static AiStreamEvent create(UUID messageId, long sequence, String eventType,
                                       String payloadJson, String payloadHash, Instant expiresAt) {
        return new AiStreamEvent(UUID.randomUUID(), messageId, sequence, eventType,
                payloadJson, payloadHash, Instant.now(), expiresAt);
    }

    public static AiStreamEvent reconstitute(UUID id, UUID messageId, long sequence, String eventType,
                                              String payloadJson, String payloadHash,
                                              Instant createdAt, Instant expiresAt) {
        return new AiStreamEvent(id, messageId, sequence, eventType, payloadJson, payloadHash, createdAt, expiresAt);
    }

    public UUID id() { return id; }
    public UUID messageId() { return messageId; }
    public long sequence() { return sequence; }
    public String eventType() { return eventType; }
    public String payloadJson() { return payloadJson; }
    public String payloadHash() { return payloadHash; }
    public Instant createdAt() { return createdAt; }
    public Instant expiresAt() { return expiresAt; }
}
