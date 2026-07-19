package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ToolCallStatus;

import java.time.Instant;
import java.util.UUID;

public class AiToolCallRecord {

    private final UUID id;
    private final UUID conversationId;
    private final UUID turnId;
    private final UUID requestMessageId;
    private UUID resultMessageId;
    private final String toolCode;
    private final String toolVersion;
    private final String handlerCode;
    private ToolCallStatus status;
    private final String requestHash;
    private String maskedArgumentsJson;
    private String serverResolvedScopeJson;
    private String resultSummaryJson;
    private UUID retrievalTraceId;
    private int resultCount;
    private boolean truncated;
    private Integer latencyMs;
    private String errorCode;
    private String errorSummaryRedacted;
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    private AiToolCallRecord(UUID id, UUID conversationId, UUID turnId, UUID requestMessageId,
                              UUID resultMessageId, String toolCode, String toolVersion, String handlerCode,
                              ToolCallStatus status, String requestHash, String maskedArgumentsJson,
                              String serverResolvedScopeJson, String resultSummaryJson,
                              UUID retrievalTraceId, int resultCount, boolean truncated,
                              Integer latencyMs, String errorCode, String errorSummaryRedacted,
                              Instant createdAt, Instant startedAt, Instant completedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.turnId = turnId;
        this.requestMessageId = requestMessageId;
        this.resultMessageId = resultMessageId;
        this.toolCode = toolCode;
        this.toolVersion = toolVersion;
        this.handlerCode = handlerCode;
        this.status = status;
        this.requestHash = requestHash;
        this.maskedArgumentsJson = maskedArgumentsJson;
        this.serverResolvedScopeJson = serverResolvedScopeJson;
        this.resultSummaryJson = resultSummaryJson;
        this.retrievalTraceId = retrievalTraceId;
        this.resultCount = resultCount;
        this.truncated = truncated;
        this.latencyMs = latencyMs;
        this.errorCode = errorCode;
        this.errorSummaryRedacted = errorSummaryRedacted;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static AiToolCallRecord request(UUID conversationId, UUID turnId, UUID requestMessageId,
                                            String toolCode, String toolVersion, String handlerCode,
                                            String requestHash, String maskedArgumentsJson,
                                            String serverResolvedScopeJson) {
        Instant now = Instant.now();
        return new AiToolCallRecord(UUID.randomUUID(), conversationId, turnId, requestMessageId,
                null, toolCode, toolVersion, handlerCode, ToolCallStatus.REQUESTED,
                requestHash, maskedArgumentsJson, serverResolvedScopeJson, "{}",
                null, 0, false, null, null, null, now, null, null);
    }

    public static AiToolCallRecord reconstitute(UUID id, UUID conversationId, UUID turnId,
                                                 UUID requestMessageId, UUID resultMessageId,
                                                 String toolCode, String toolVersion, String handlerCode,
                                                 ToolCallStatus status, String requestHash,
                                                 String maskedArgumentsJson, String serverResolvedScopeJson,
                                                 String resultSummaryJson, UUID retrievalTraceId,
                                                 int resultCount, boolean truncated, Integer latencyMs,
                                                 String errorCode, String errorSummaryRedacted,
                                                 Instant createdAt, Instant startedAt, Instant completedAt) {
        return new AiToolCallRecord(id, conversationId, turnId, requestMessageId, resultMessageId,
                toolCode, toolVersion, handlerCode, status, requestHash, maskedArgumentsJson,
                serverResolvedScopeJson, resultSummaryJson, retrievalTraceId, resultCount, truncated,
                latencyMs, errorCode, errorSummaryRedacted, createdAt, startedAt, completedAt);
    }

    public void markRunning() {
        this.status = ToolCallStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void markSucceeded(UUID resultMessageId, UUID retrievalTraceId, int resultCount,
                               boolean truncated, String resultSummaryJson, Integer latencyMs) {
        Instant now = Instant.now();
        this.status = ToolCallStatus.SUCCEEDED;
        this.resultMessageId = resultMessageId;
        this.retrievalTraceId = retrievalTraceId;
        this.resultCount = resultCount;
        this.truncated = truncated;
        this.resultSummaryJson = resultSummaryJson;
        this.latencyMs = latencyMs;
        this.completedAt = now;
    }

    public void markFailed(String errorCode, String errorSummary, Integer latencyMs) {
        Instant now = Instant.now();
        this.status = ToolCallStatus.FAILED;
        this.errorCode = errorCode;
        this.errorSummaryRedacted = errorSummary;
        this.latencyMs = latencyMs;
        this.completedAt = now;
    }

    public void markCancelled() {
        this.status = ToolCallStatus.CANCELLED;
        this.completedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID conversationId() { return conversationId; }
    public UUID turnId() { return turnId; }
    public UUID requestMessageId() { return requestMessageId; }
    public UUID resultMessageId() { return resultMessageId; }
    public String toolCode() { return toolCode; }
    public String toolVersion() { return toolVersion; }
    public String handlerCode() { return handlerCode; }
    public ToolCallStatus status() { return status; }
    public String requestHash() { return requestHash; }
    public String maskedArgumentsJson() { return maskedArgumentsJson; }
    public String serverResolvedScopeJson() { return serverResolvedScopeJson; }
    public String resultSummaryJson() { return resultSummaryJson; }
    public UUID retrievalTraceId() { return retrievalTraceId; }
    public int resultCount() { return resultCount; }
    public boolean truncated() { return truncated; }
    public Integer latencyMs() { return latencyMs; }
    public String errorCode() { return errorCode; }
    public String errorSummaryRedacted() { return errorSummaryRedacted; }
    public Instant createdAt() { return createdAt; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
}
