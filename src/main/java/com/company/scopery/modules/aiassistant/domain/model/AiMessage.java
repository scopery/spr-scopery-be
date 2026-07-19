package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ContentFormat;
import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.enums.MessageStatus;
import com.company.scopery.modules.aiassistant.domain.enums.ResponseMode;

import java.time.Instant;
import java.util.UUID;

public class AiMessage {

    private final UUID id;
    private final UUID conversationId;
    private final UUID turnId;
    private UUID parentMessageId;
    private String idempotencyKey;
    private final int sequenceInConversation;
    private final MessageRole role;
    private MessageStatus status;
    private ContentFormat contentFormat;
    private String content;
    private ResponseMode responseMode;
    private String modelProvider;
    private String modelName;
    private String modelDeployment;
    private String promptProfileCode;
    private int inputTokenCount;
    private int outputTokenCount;
    private Integer latencyMs;
    private String finishReason;
    private String errorCode;
    private String errorSummaryRedacted;
    private String traceId;
    private String correlationId;
    private Instant cancelRequestedAt;
    private UUID cancelRequestedBy;
    private final Instant createdAt;
    private UUID createdBy;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;
    private Instant updatedAt;
    private UUID updatedBy;
    private long version;

    private AiMessage(UUID id, UUID conversationId, UUID turnId, UUID parentMessageId,
                      String idempotencyKey, int sequenceInConversation, MessageRole role,
                      MessageStatus status, ContentFormat contentFormat, String content,
                      ResponseMode responseMode, String modelProvider, String modelName,
                      String modelDeployment, String promptProfileCode,
                      int inputTokenCount, int outputTokenCount, Integer latencyMs,
                      String finishReason, String errorCode, String errorSummaryRedacted,
                      String traceId, String correlationId,
                      Instant cancelRequestedAt, UUID cancelRequestedBy,
                      Instant createdAt, UUID createdBy, Instant startedAt,
                      Instant completedAt, Instant cancelledAt,
                      Instant updatedAt, UUID updatedBy, long version) {
        this.id = id;
        this.conversationId = conversationId;
        this.turnId = turnId;
        this.parentMessageId = parentMessageId;
        this.idempotencyKey = idempotencyKey;
        this.sequenceInConversation = sequenceInConversation;
        this.role = role;
        this.status = status;
        this.contentFormat = contentFormat;
        this.content = content;
        this.responseMode = responseMode;
        this.modelProvider = modelProvider;
        this.modelName = modelName;
        this.modelDeployment = modelDeployment;
        this.promptProfileCode = promptProfileCode;
        this.inputTokenCount = inputTokenCount;
        this.outputTokenCount = outputTokenCount;
        this.latencyMs = latencyMs;
        this.finishReason = finishReason;
        this.errorCode = errorCode;
        this.errorSummaryRedacted = errorSummaryRedacted;
        this.traceId = traceId;
        this.correlationId = correlationId;
        this.cancelRequestedAt = cancelRequestedAt;
        this.cancelRequestedBy = cancelRequestedBy;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static AiMessage createUser(UUID conversationId, UUID turnId, int sequence,
                                       String content, String idempotencyKey, String traceId) {
        Instant now = Instant.now();
        return new AiMessage(UUID.randomUUID(), conversationId, turnId, null, idempotencyKey,
                sequence, MessageRole.USER, MessageStatus.COMPLETED, ContentFormat.MARKDOWN,
                content, null, null, null, null, null,
                0, 0, null, null, null, null, traceId, null,
                null, null, now, null, now, now, null, now, null, 0L);
    }

    public static AiMessage createAssistantPlaceholder(UUID conversationId, UUID turnId,
                                                       int sequence, String traceId) {
        Instant now = Instant.now();
        return new AiMessage(UUID.randomUUID(), conversationId, turnId, null, null,
                sequence, MessageRole.ASSISTANT, MessageStatus.QUEUED, ContentFormat.MARKDOWN,
                null, null, null, null, null, null,
                0, 0, null, null, null, null, traceId, null,
                null, null, now, null, null, null, null, now, null, 0L);
    }

    public static AiMessage createToolRequest(UUID conversationId, UUID turnId, int sequence,
                                              String toolRequestJson, String traceId) {
        Instant now = Instant.now();
        return new AiMessage(UUID.randomUUID(), conversationId, turnId, null, null,
                sequence, MessageRole.TOOL_REQUEST, MessageStatus.COMPLETED, ContentFormat.JSON_SUMMARY,
                toolRequestJson, null, null, null, null, null,
                0, 0, null, null, null, null, traceId, null,
                null, null, now, null, now, now, null, now, null, 0L);
    }

    public static AiMessage createToolResult(UUID conversationId, UUID turnId, int sequence,
                                             String toolResultJson, String traceId) {
        Instant now = Instant.now();
        return new AiMessage(UUID.randomUUID(), conversationId, turnId, null, null,
                sequence, MessageRole.TOOL_RESULT, MessageStatus.COMPLETED, ContentFormat.JSON_SUMMARY,
                toolResultJson, null, null, null, null, null,
                0, 0, null, null, null, null, traceId, null,
                null, null, now, null, now, now, null, now, null, 0L);
    }

    public static AiMessage reconstitute(UUID id, UUID conversationId, UUID turnId, UUID parentMessageId,
                                         String idempotencyKey, int sequenceInConversation, MessageRole role,
                                         MessageStatus status, ContentFormat contentFormat, String content,
                                         ResponseMode responseMode, String modelProvider, String modelName,
                                         String modelDeployment, String promptProfileCode,
                                         int inputTokenCount, int outputTokenCount, Integer latencyMs,
                                         String finishReason, String errorCode, String errorSummaryRedacted,
                                         String traceId, String correlationId,
                                         Instant cancelRequestedAt, UUID cancelRequestedBy,
                                         Instant createdAt, UUID createdBy, Instant startedAt,
                                         Instant completedAt, Instant cancelledAt,
                                         Instant updatedAt, UUID updatedBy, long version) {
        return new AiMessage(id, conversationId, turnId, parentMessageId, idempotencyKey,
                sequenceInConversation, role, status, contentFormat, content, responseMode,
                modelProvider, modelName, modelDeployment, promptProfileCode,
                inputTokenCount, outputTokenCount, latencyMs, finishReason,
                errorCode, errorSummaryRedacted, traceId, correlationId,
                cancelRequestedAt, cancelRequestedBy, createdAt, createdBy,
                startedAt, completedAt, cancelledAt, updatedAt, updatedBy, version);
    }

    public void markContextualizing() {
        this.status = MessageStatus.CONTEXTUALIZING;
        this.startedAt = Instant.now();
        this.updatedAt = this.startedAt;
    }

    public void markRetrieving() {
        this.status = MessageStatus.RETRIEVING;
        this.updatedAt = Instant.now();
    }

    public void markGenerating() {
        this.status = MessageStatus.GENERATING;
        this.updatedAt = Instant.now();
    }

    public void markStreaming() {
        this.status = MessageStatus.STREAMING;
        this.updatedAt = Instant.now();
    }

    public void markCancelRequested(UUID requestedBy) {
        this.status = MessageStatus.CANCEL_REQUESTED;
        this.cancelRequestedAt = Instant.now();
        this.cancelRequestedBy = requestedBy;
        this.updatedAt = this.cancelRequestedAt;
    }

    public void markCompleted(String content, ResponseMode responseMode,
                              String modelProvider, String modelName, String modelDeployment,
                              String promptProfileCode, int inputTokens, int outputTokens,
                              Integer latencyMs, String finishReason) {
        Instant now = Instant.now();
        this.status = MessageStatus.COMPLETED;
        this.content = content;
        this.responseMode = responseMode;
        this.modelProvider = modelProvider;
        this.modelName = modelName;
        this.modelDeployment = modelDeployment;
        this.promptProfileCode = promptProfileCode;
        this.inputTokenCount = inputTokens;
        this.outputTokenCount = outputTokens;
        this.latencyMs = latencyMs;
        this.finishReason = finishReason;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public void markFailed(String errorCode, String errorSummary) {
        Instant now = Instant.now();
        this.status = MessageStatus.FAILED;
        this.errorCode = errorCode;
        this.errorSummaryRedacted = errorSummary != null && errorSummary.length() > 500
                ? errorSummary.substring(0, 500) : errorSummary;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public void markCancelled() {
        Instant now = Instant.now();
        this.status = MessageStatus.CANCELLED;
        this.cancelledAt = now;
        this.updatedAt = now;
    }

    public void markBlocked(String reason) {
        Instant now = Instant.now();
        this.status = MessageStatus.BLOCKED;
        this.errorSummaryRedacted = reason;
        this.completedAt = now;
        this.updatedAt = now;
    }

    public boolean isCancelRequested() {
        return this.status == MessageStatus.CANCEL_REQUESTED;
    }

    public boolean isFinal() {
        return this.status == MessageStatus.COMPLETED
                || this.status == MessageStatus.FAILED
                || this.status == MessageStatus.CANCELLED
                || this.status == MessageStatus.BLOCKED;
    }

    public UUID id() { return id; }
    public UUID conversationId() { return conversationId; }
    public UUID turnId() { return turnId; }
    public UUID parentMessageId() { return parentMessageId; }
    public String idempotencyKey() { return idempotencyKey; }
    public int sequenceInConversation() { return sequenceInConversation; }
    public MessageRole role() { return role; }
    public MessageStatus status() { return status; }
    public ContentFormat contentFormat() { return contentFormat; }
    public String content() { return content; }
    public ResponseMode responseMode() { return responseMode; }
    public String modelProvider() { return modelProvider; }
    public String modelName() { return modelName; }
    public String modelDeployment() { return modelDeployment; }
    public String promptProfileCode() { return promptProfileCode; }
    public int inputTokenCount() { return inputTokenCount; }
    public int outputTokenCount() { return outputTokenCount; }
    public Integer latencyMs() { return latencyMs; }
    public String finishReason() { return finishReason; }
    public String errorCode() { return errorCode; }
    public String errorSummaryRedacted() { return errorSummaryRedacted; }
    public String traceId() { return traceId; }
    public String correlationId() { return correlationId; }
    public Instant cancelRequestedAt() { return cancelRequestedAt; }
    public UUID cancelRequestedBy() { return cancelRequestedBy; }
    public Instant createdAt() { return createdAt; }
    public UUID createdBy() { return createdBy; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public Instant cancelledAt() { return cancelledAt; }
    public Instant updatedAt() { return updatedAt; }
    public UUID updatedBy() { return updatedBy; }
    public long version() { return version; }
}
