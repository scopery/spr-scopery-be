package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.MemorySummaryStatus;

import java.time.Instant;
import java.util.UUID;

public class AiMemorySummary {

    private final UUID id;
    private final UUID conversationId;
    private final int summaryVersion;
    private final String strategyCode;
    private MemorySummaryStatus status;
    private final int coveredThroughMessageSequence;
    private final int sourceMessageCount;
    private final int estimatedTokenCount;
    private final String summaryText;
    private final String permissionSignature;
    private final String summaryHash;
    private String modelProvider;
    private String modelName;
    private String promptProfileCode;
    private final Instant createdAt;
    private UUID createdBy;
    private Instant invalidatedAt;
    private String invalidationReasonCode;

    private AiMemorySummary(UUID id, UUID conversationId, int summaryVersion, String strategyCode,
                             MemorySummaryStatus status, int coveredThroughMessageSequence,
                             int sourceMessageCount, int estimatedTokenCount, String summaryText,
                             String permissionSignature, String summaryHash,
                             String modelProvider, String modelName, String promptProfileCode,
                             Instant createdAt, UUID createdBy, Instant invalidatedAt,
                             String invalidationReasonCode) {
        this.id = id;
        this.conversationId = conversationId;
        this.summaryVersion = summaryVersion;
        this.strategyCode = strategyCode;
        this.status = status;
        this.coveredThroughMessageSequence = coveredThroughMessageSequence;
        this.sourceMessageCount = sourceMessageCount;
        this.estimatedTokenCount = estimatedTokenCount;
        this.summaryText = summaryText;
        this.permissionSignature = permissionSignature;
        this.summaryHash = summaryHash;
        this.modelProvider = modelProvider;
        this.modelName = modelName;
        this.promptProfileCode = promptProfileCode;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.invalidatedAt = invalidatedAt;
        this.invalidationReasonCode = invalidationReasonCode;
    }

    public static AiMemorySummary create(UUID conversationId, int summaryVersion,
                                          int coveredThroughMessageSequence, int sourceMessageCount,
                                          int estimatedTokenCount, String summaryText,
                                          String permissionSignature, String summaryHash,
                                          String modelProvider, String modelName, String promptProfileCode) {
        Instant now = Instant.now();
        return new AiMemorySummary(UUID.randomUUID(), conversationId, summaryVersion, "summary-v1",
                MemorySummaryStatus.ACTIVE, coveredThroughMessageSequence, sourceMessageCount,
                estimatedTokenCount, summaryText, permissionSignature, summaryHash,
                modelProvider, modelName, promptProfileCode, now, null, null, null);
    }

    public static AiMemorySummary reconstitute(UUID id, UUID conversationId, int summaryVersion,
                                                String strategyCode, MemorySummaryStatus status,
                                                int coveredThroughMessageSequence, int sourceMessageCount,
                                                int estimatedTokenCount, String summaryText,
                                                String permissionSignature, String summaryHash,
                                                String modelProvider, String modelName, String promptProfileCode,
                                                Instant createdAt, UUID createdBy,
                                                Instant invalidatedAt, String invalidationReasonCode) {
        return new AiMemorySummary(id, conversationId, summaryVersion, strategyCode, status,
                coveredThroughMessageSequence, sourceMessageCount, estimatedTokenCount, summaryText,
                permissionSignature, summaryHash, modelProvider, modelName, promptProfileCode,
                createdAt, createdBy, invalidatedAt, invalidationReasonCode);
    }

    public void supersede() {
        this.status = MemorySummaryStatus.SUPERSEDED;
        this.invalidatedAt = Instant.now();
        this.invalidationReasonCode = "NEW_VERSION_CREATED";
    }

    public UUID id() { return id; }
    public UUID conversationId() { return conversationId; }
    public int summaryVersion() { return summaryVersion; }
    public String strategyCode() { return strategyCode; }
    public MemorySummaryStatus status() { return status; }
    public int coveredThroughMessageSequence() { return coveredThroughMessageSequence; }
    public int sourceMessageCount() { return sourceMessageCount; }
    public int estimatedTokenCount() { return estimatedTokenCount; }
    public String summaryText() { return summaryText; }
    public String permissionSignature() { return permissionSignature; }
    public String summaryHash() { return summaryHash; }
    public String modelProvider() { return modelProvider; }
    public String modelName() { return modelName; }
    public String promptProfileCode() { return promptProfileCode; }
    public Instant createdAt() { return createdAt; }
    public UUID createdBy() { return createdBy; }
    public Instant invalidatedAt() { return invalidatedAt; }
    public String invalidationReasonCode() { return invalidationReasonCode; }
}
