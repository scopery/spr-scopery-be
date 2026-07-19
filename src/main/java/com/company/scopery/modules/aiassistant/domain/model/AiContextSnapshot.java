package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ContextStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AiContextSnapshot {

    private final UUID id;
    private final UUID conversationId;
    private final UUID assistantMessageId;
    private final UUID turnId;
    private final UUID actorId;
    private final UUID workspaceId;
    private UUID projectId;
    private String route;
    private String pageCode;
    private Integer pageMetadataVersion;
    private String entityType;
    private UUID entityId;
    private Long entityVersion;
    private String selectedActionCode;
    private String tabCode;
    private String locale;
    private String timezone;
    private int clientContextVersion;
    private List<String> clientVisibleFieldCodes;
    private List<String> clientReportedActionCodes;
    private List<String> serverVisibleFieldCodes;
    private List<String> availableActionCodes;
    private String disabledActionReasonsJson;
    private final String permissionSignature;
    private String clientContextHash;
    private final String contextHash;
    private String serverContextJson;
    private ContextStatus contextStatus;
    private String invalidationReasonCode;
    private final Instant createdAt;
    private final Instant expiresAt;
    private Instant invalidatedAt;

    private AiContextSnapshot(UUID id, UUID conversationId, UUID assistantMessageId, UUID turnId,
                              UUID actorId, UUID workspaceId, UUID projectId, String route,
                              String pageCode, Integer pageMetadataVersion, String entityType,
                              UUID entityId, Long entityVersion, String selectedActionCode, String tabCode,
                              String locale, String timezone, int clientContextVersion,
                              List<String> clientVisibleFieldCodes, List<String> clientReportedActionCodes,
                              List<String> serverVisibleFieldCodes, List<String> availableActionCodes,
                              String disabledActionReasonsJson, String permissionSignature,
                              String clientContextHash, String contextHash, String serverContextJson,
                              ContextStatus contextStatus, String invalidationReasonCode,
                              Instant createdAt, Instant expiresAt, Instant invalidatedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.assistantMessageId = assistantMessageId;
        this.turnId = turnId;
        this.actorId = actorId;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.route = route;
        this.pageCode = pageCode;
        this.pageMetadataVersion = pageMetadataVersion;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityVersion = entityVersion;
        this.selectedActionCode = selectedActionCode;
        this.tabCode = tabCode;
        this.locale = locale;
        this.timezone = timezone;
        this.clientContextVersion = clientContextVersion;
        this.clientVisibleFieldCodes = clientVisibleFieldCodes;
        this.clientReportedActionCodes = clientReportedActionCodes;
        this.serverVisibleFieldCodes = serverVisibleFieldCodes;
        this.availableActionCodes = availableActionCodes;
        this.disabledActionReasonsJson = disabledActionReasonsJson;
        this.permissionSignature = permissionSignature;
        this.clientContextHash = clientContextHash;
        this.contextHash = contextHash;
        this.serverContextJson = serverContextJson;
        this.contextStatus = contextStatus;
        this.invalidationReasonCode = invalidationReasonCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.invalidatedAt = invalidatedAt;
    }

    public static AiContextSnapshot create(UUID conversationId, UUID assistantMessageId, UUID turnId,
                                           UUID actorId, UUID workspaceId, UUID projectId,
                                           String permissionSignature, String contextHash,
                                           String serverContextJson, Instant expiresAt) {
        Instant now = Instant.now();
        return new AiContextSnapshot(UUID.randomUUID(), conversationId, assistantMessageId, turnId,
                actorId, workspaceId, projectId, null, null, null, null, null, null,
                null, null, "en-US", "UTC", 1, List.of(), List.of(), List.of(), List.of(),
                "{}", permissionSignature, null, contextHash, serverContextJson,
                ContextStatus.VALID, null, now, expiresAt, null);
    }

    public static AiContextSnapshot reconstitute(UUID id, UUID conversationId, UUID assistantMessageId,
                                                 UUID turnId, UUID actorId, UUID workspaceId, UUID projectId,
                                                 String route, String pageCode, Integer pageMetadataVersion,
                                                 String entityType, UUID entityId, Long entityVersion,
                                                 String selectedActionCode, String tabCode,
                                                 String locale, String timezone, int clientContextVersion,
                                                 List<String> clientVisibleFieldCodes,
                                                 List<String> clientReportedActionCodes,
                                                 List<String> serverVisibleFieldCodes,
                                                 List<String> availableActionCodes,
                                                 String disabledActionReasonsJson,
                                                 String permissionSignature, String clientContextHash,
                                                 String contextHash, String serverContextJson,
                                                 ContextStatus contextStatus, String invalidationReasonCode,
                                                 Instant createdAt, Instant expiresAt, Instant invalidatedAt) {
        return new AiContextSnapshot(id, conversationId, assistantMessageId, turnId, actorId,
                workspaceId, projectId, route, pageCode, pageMetadataVersion, entityType, entityId,
                entityVersion, selectedActionCode, tabCode, locale, timezone, clientContextVersion,
                clientVisibleFieldCodes, clientReportedActionCodes, serverVisibleFieldCodes,
                availableActionCodes, disabledActionReasonsJson, permissionSignature,
                clientContextHash, contextHash, serverContextJson, contextStatus,
                invalidationReasonCode, createdAt, expiresAt, invalidatedAt);
    }

    public void invalidate(String reasonCode) {
        this.contextStatus = ContextStatus.INVALIDATED;
        this.invalidationReasonCode = reasonCode;
        this.invalidatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID conversationId() { return conversationId; }
    public UUID assistantMessageId() { return assistantMessageId; }
    public UUID turnId() { return turnId; }
    public UUID actorId() { return actorId; }
    public UUID workspaceId() { return workspaceId; }
    public UUID projectId() { return projectId; }
    public String route() { return route; }
    public String pageCode() { return pageCode; }
    public Integer pageMetadataVersion() { return pageMetadataVersion; }
    public String entityType() { return entityType; }
    public UUID entityId() { return entityId; }
    public Long entityVersion() { return entityVersion; }
    public String selectedActionCode() { return selectedActionCode; }
    public String tabCode() { return tabCode; }
    public String locale() { return locale; }
    public String timezone() { return timezone; }
    public int clientContextVersion() { return clientContextVersion; }
    public List<String> clientVisibleFieldCodes() { return clientVisibleFieldCodes; }
    public List<String> clientReportedActionCodes() { return clientReportedActionCodes; }
    public List<String> serverVisibleFieldCodes() { return serverVisibleFieldCodes; }
    public List<String> availableActionCodes() { return availableActionCodes; }
    public String disabledActionReasonsJson() { return disabledActionReasonsJson; }
    public String permissionSignature() { return permissionSignature; }
    public String clientContextHash() { return clientContextHash; }
    public String contextHash() { return contextHash; }
    public String serverContextJson() { return serverContextJson; }
    public ContextStatus contextStatus() { return contextStatus; }
    public String invalidationReasonCode() { return invalidationReasonCode; }
    public Instant createdAt() { return createdAt; }
    public Instant expiresAt() { return expiresAt; }
    public Instant invalidatedAt() { return invalidatedAt; }
}
