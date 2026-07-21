package com.company.scopery.modules.aiaction.request.domain.model;

import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionRequestStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionRequest {

    private final UUID id;
    private final UUID workspaceId;
    private UUID projectId;
    private final UUID initiatedByUserId;
    private final AiActionOriginType originType;
    private String originConversationId;
    private String originMessageId;
    private String originTurnId;
    private String originSuggestionRef;
    private String legacyPhase21SuggestionId;
    private final String intentSummary;
    private AiActionRequestStatus status;
    private final String idempotencyKey;
    private String requestHash;
    private UUID latestPlanId;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiActionRequest(UUID id, UUID workspaceId, UUID projectId, UUID initiatedByUserId,
                             AiActionOriginType originType, String originConversationId,
                             String originMessageId, String originTurnId,
                             String originSuggestionRef, String legacyPhase21SuggestionId,
                             String intentSummary, AiActionRequestStatus status,
                             String idempotencyKey, String requestHash, UUID latestPlanId,
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.initiatedByUserId = initiatedByUserId;
        this.originType = originType;
        this.originConversationId = originConversationId;
        this.originMessageId = originMessageId;
        this.originTurnId = originTurnId;
        this.originSuggestionRef = originSuggestionRef;
        this.legacyPhase21SuggestionId = legacyPhase21SuggestionId;
        this.intentSummary = intentSummary;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.latestPlanId = latestPlanId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiActionRequest create(UUID workspaceId, UUID projectId, UUID initiatedByUserId,
                                          AiActionOriginType originType, String originSuggestionRef,
                                          String originConversationId, String originMessageId,
                                          String originTurnId, String legacyPhase21SuggestionId,
                                          String intentSummary, String idempotencyKey, String requestHash) {
        Instant now = Instant.now();
        return new AiActionRequest(UUID.randomUUID(), workspaceId, projectId, initiatedByUserId,
                originType, originConversationId, originMessageId, originTurnId,
                originSuggestionRef, legacyPhase21SuggestionId,
                intentSummary, AiActionRequestStatus.RECEIVED,
                idempotencyKey, requestHash, null, now, now);
    }

    public static AiActionRequest reconstitute(UUID id, UUID workspaceId, UUID projectId,
                                                UUID initiatedByUserId, AiActionOriginType originType,
                                                String originConversationId, String originMessageId,
                                                String originTurnId, String originSuggestionRef,
                                                String legacyPhase21SuggestionId, String intentSummary,
                                                AiActionRequestStatus status, String idempotencyKey,
                                                String requestHash, UUID latestPlanId,
                                                Instant createdAt, Instant updatedAt) {
        return new AiActionRequest(id, workspaceId, projectId, initiatedByUserId, originType,
                originConversationId, originMessageId, originTurnId, originSuggestionRef,
                legacyPhase21SuggestionId, intentSummary, status, idempotencyKey,
                requestHash, latestPlanId, createdAt, updatedAt);
    }

    public void markPlanning() {
        this.status = AiActionRequestStatus.PLANNING;
        this.updatedAt = Instant.now();
    }

    public void markPlanned(UUID planId) {
        this.latestPlanId = planId;
        this.status = AiActionRequestStatus.PLANNED;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AiActionRequestStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void markCancelled() {
        this.status = AiActionRequestStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public UUID id()                        { return id; }
    public UUID workspaceId()               { return workspaceId; }
    public UUID projectId()                 { return projectId; }
    public UUID initiatedByUserId()         { return initiatedByUserId; }
    public AiActionOriginType originType()  { return originType; }
    public String originConversationId()    { return originConversationId; }
    public String originMessageId()         { return originMessageId; }
    public String originTurnId()            { return originTurnId; }
    public String originSuggestionRef()     { return originSuggestionRef; }
    public String legacyPhase21SuggestionId() { return legacyPhase21SuggestionId; }
    public String intentSummary()           { return intentSummary; }
    public AiActionRequestStatus status()   { return status; }
    public String idempotencyKey()          { return idempotencyKey; }
    public String requestHash()             { return requestHash; }
    public UUID latestPlanId()              { return latestPlanId; }
    public Instant createdAt()              { return createdAt; }
    public Instant updatedAt()              { return updatedAt; }
}
