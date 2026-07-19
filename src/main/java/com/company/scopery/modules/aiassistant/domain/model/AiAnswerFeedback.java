package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiAnswerFeedback {

    private final UUID id;
    private final UUID conversationId;
    private final UUID messageId;
    private final UUID actorId;
    private String rating;
    private String reasonCode;
    private String comment;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;

    private AiAnswerFeedback(UUID id, UUID conversationId, UUID messageId, UUID actorId,
                              String rating, String reasonCode, String comment,
                              Instant createdAt, Instant updatedAt, long version) {
        this.id = id;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.actorId = actorId;
        this.rating = rating;
        this.reasonCode = reasonCode;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static AiAnswerFeedback create(UUID conversationId, UUID messageId, UUID actorId,
                                           String rating, String reasonCode, String comment) {
        Instant now = Instant.now();
        return new AiAnswerFeedback(UUID.randomUUID(), conversationId, messageId, actorId,
                rating, reasonCode, comment, now, now, 0L);
    }

    public static AiAnswerFeedback reconstitute(UUID id, UUID conversationId, UUID messageId, UUID actorId,
                                                 String rating, String reasonCode, String comment,
                                                 Instant createdAt, Instant updatedAt, long version) {
        return new AiAnswerFeedback(id, conversationId, messageId, actorId, rating, reasonCode, comment,
                createdAt, updatedAt, version);
    }

    public UUID id() { return id; }
    public UUID conversationId() { return conversationId; }
    public UUID messageId() { return messageId; }
    public UUID actorId() { return actorId; }
    public String rating() { return rating; }
    public String reasonCode() { return reasonCode; }
    public String comment() { return comment; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long version() { return version; }
}
