package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.CapabilityLevel;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;
import com.company.scopery.modules.aiassistant.domain.enums.TitleSource;

import java.time.Instant;
import java.util.UUID;

public class AiConversation {

    private final UUID id;
    private final UUID workspaceId;
    private UUID projectId;
    private final UUID ownerUserId;
    private final ConversationType conversationType;
    private final CapabilityLevel capabilityLevel;
    private UUID assistantAgentId;
    private ConversationStatus status;
    private String title;
    private TitleSource titleSource;
    private final String retentionPolicyCode;
    private Instant lastMessageAt;
    private Integer lastMemorySummaryVersion;
    private Instant archivedAt;
    private Instant deletedAt;
    private final Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private long version;

    private AiConversation(UUID id, UUID workspaceId, UUID projectId, UUID ownerUserId,
                           ConversationType conversationType, CapabilityLevel capabilityLevel,
                           UUID assistantAgentId, ConversationStatus status,
                           String title, TitleSource titleSource, String retentionPolicyCode,
                           Instant lastMessageAt, Integer lastMemorySummaryVersion,
                           Instant archivedAt, Instant deletedAt,
                           Instant createdAt, UUID createdBy, Instant updatedAt, UUID updatedBy, long version) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.ownerUserId = ownerUserId;
        this.conversationType = conversationType;
        this.capabilityLevel = capabilityLevel;
        this.assistantAgentId = assistantAgentId;
        this.status = status;
        this.title = title;
        this.titleSource = titleSource;
        this.retentionPolicyCode = retentionPolicyCode;
        this.lastMessageAt = lastMessageAt;
        this.lastMemorySummaryVersion = lastMemorySummaryVersion;
        this.archivedAt = archivedAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static AiConversation create(UUID workspaceId, UUID projectId, UUID ownerUserId,
                                        ConversationType type, CapabilityLevel level,
                                        UUID assistantAgentId, String title) {
        Instant now = Instant.now();
        return new AiConversation(UUID.randomUUID(), workspaceId, projectId, ownerUserId,
                type, level, assistantAgentId, ConversationStatus.ACTIVE,
                title, TitleSource.AUTO, "AI_ASSISTANT_DEFAULT_180D",
                null, null, null, null, now, null, now, null, 0L);
    }

    public static AiConversation reconstitute(UUID id, UUID workspaceId, UUID projectId, UUID ownerUserId,
                                              ConversationType conversationType, CapabilityLevel capabilityLevel,
                                              UUID assistantAgentId, ConversationStatus status,
                                              String title, TitleSource titleSource, String retentionPolicyCode,
                                              Instant lastMessageAt, Integer lastMemorySummaryVersion,
                                              Instant archivedAt, Instant deletedAt,
                                              Instant createdAt, UUID createdBy, Instant updatedAt, UUID updatedBy, long version) {
        return new AiConversation(id, workspaceId, projectId, ownerUserId, conversationType, capabilityLevel,
                assistantAgentId, status, title, titleSource, retentionPolicyCode,
                lastMessageAt, lastMemorySummaryVersion, archivedAt, deletedAt,
                createdAt, createdBy, updatedAt, updatedBy, version);
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
        this.titleSource = TitleSource.USER;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        if (this.status == ConversationStatus.ARCHIVED) return;
        this.status = ConversationStatus.ARCHIVED;
        this.archivedAt = Instant.now();
        this.updatedAt = this.archivedAt;
    }

    public void softDelete() {
        this.status = ConversationStatus.DELETED;
        this.deletedAt = Instant.now();
        this.updatedAt = this.deletedAt;
    }

    public void touchLastMessage(Instant at) {
        this.lastMessageAt = at;
        this.updatedAt = at;
    }

    public void updateMemorySummaryVersion(int version) {
        this.lastMemorySummaryVersion = version;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID workspaceId() { return workspaceId; }
    public UUID projectId() { return projectId; }
    public UUID ownerUserId() { return ownerUserId; }
    public ConversationType conversationType() { return conversationType; }
    public CapabilityLevel capabilityLevel() { return capabilityLevel; }
    public UUID assistantAgentId() { return assistantAgentId; }
    public ConversationStatus status() { return status; }
    public String title() { return title; }
    public TitleSource titleSource() { return titleSource; }
    public String retentionPolicyCode() { return retentionPolicyCode; }
    public Instant lastMessageAt() { return lastMessageAt; }
    public Integer lastMemorySummaryVersion() { return lastMemorySummaryVersion; }
    public Instant archivedAt() { return archivedAt; }
    public Instant deletedAt() { return deletedAt; }
    public Instant createdAt() { return createdAt; }
    public UUID createdBy() { return createdBy; }
    public Instant updatedAt() { return updatedAt; }
    public UUID updatedBy() { return updatedBy; }
    public long version() { return version; }
}
