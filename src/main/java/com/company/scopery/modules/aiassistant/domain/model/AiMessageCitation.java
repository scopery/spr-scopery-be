package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AiMessageCitation {

    private final UUID id;
    private final UUID messageId;
    private final int ordinal;
    private UUID retrievalTraceId;
    private UUID knowledgeChunkId;
    private final String sourceType;
    private final UUID sourceRefId;
    private UUID sourceVersionRefId;
    private final String title;
    private List<String> headingPath;
    private String quotedFragment;
    private String appRoute;
    private final String permissionSignature;
    private final String accessValidationResult;
    private final Instant accessValidatedAt;
    private final Instant createdAt;

    private AiMessageCitation(UUID id, UUID messageId, int ordinal, UUID retrievalTraceId,
                               UUID knowledgeChunkId, String sourceType, UUID sourceRefId,
                               UUID sourceVersionRefId, String title, List<String> headingPath,
                               String quotedFragment, String appRoute, String permissionSignature,
                               String accessValidationResult, Instant accessValidatedAt, Instant createdAt) {
        this.id = id;
        this.messageId = messageId;
        this.ordinal = ordinal;
        this.retrievalTraceId = retrievalTraceId;
        this.knowledgeChunkId = knowledgeChunkId;
        this.sourceType = sourceType;
        this.sourceRefId = sourceRefId;
        this.sourceVersionRefId = sourceVersionRefId;
        this.title = title;
        this.headingPath = headingPath;
        this.quotedFragment = quotedFragment;
        this.appRoute = appRoute;
        this.permissionSignature = permissionSignature;
        this.accessValidationResult = accessValidationResult;
        this.accessValidatedAt = accessValidatedAt;
        this.createdAt = createdAt;
    }

    public static AiMessageCitation create(UUID messageId, int ordinal, UUID retrievalTraceId,
                                           UUID knowledgeChunkId, String sourceType, UUID sourceRefId,
                                           UUID sourceVersionRefId, String title, List<String> headingPath,
                                           String quotedFragment, String permissionSignature,
                                           String accessValidationResult) {
        Instant now = Instant.now();
        return new AiMessageCitation(UUID.randomUUID(), messageId, ordinal, retrievalTraceId,
                knowledgeChunkId, sourceType, sourceRefId, sourceVersionRefId, title, headingPath,
                quotedFragment, null, permissionSignature, accessValidationResult, now, now);
    }

    public static AiMessageCitation reconstitute(UUID id, UUID messageId, int ordinal,
                                                  UUID retrievalTraceId, UUID knowledgeChunkId,
                                                  String sourceType, UUID sourceRefId, UUID sourceVersionRefId,
                                                  String title, List<String> headingPath, String quotedFragment,
                                                  String appRoute, String permissionSignature,
                                                  String accessValidationResult, Instant accessValidatedAt,
                                                  Instant createdAt) {
        return new AiMessageCitation(id, messageId, ordinal, retrievalTraceId, knowledgeChunkId,
                sourceType, sourceRefId, sourceVersionRefId, title, headingPath, quotedFragment,
                appRoute, permissionSignature, accessValidationResult, accessValidatedAt, createdAt);
    }

    public UUID id() { return id; }
    public UUID messageId() { return messageId; }
    public int ordinal() { return ordinal; }
    public UUID retrievalTraceId() { return retrievalTraceId; }
    public UUID knowledgeChunkId() { return knowledgeChunkId; }
    public String sourceType() { return sourceType; }
    public UUID sourceRefId() { return sourceRefId; }
    public UUID sourceVersionRefId() { return sourceVersionRefId; }
    public String title() { return title; }
    public List<String> headingPath() { return headingPath; }
    public String quotedFragment() { return quotedFragment; }
    public String appRoute() { return appRoute; }
    public String permissionSignature() { return permissionSignature; }
    public String accessValidationResult() { return accessValidationResult; }
    public Instant accessValidatedAt() { return accessValidatedAt; }
    public Instant createdAt() { return createdAt; }
}
