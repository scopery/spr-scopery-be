package com.company.scopery.modules.notification.notificationitem.domain.model;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;

import java.time.Instant;
import java.util.UUID;

public class NotificationItem {

    private final UUID id;
    private final UUID recipientUserId;
    private final UUID eventDefinitionId;
    private final String sourceSystem;
    private final String sourceResourceType;
    private final UUID sourceResourceId;
    private final UUID organizationId;
    private final UUID workspaceId;
    private final UUID projectId;
    private final String title;
    private final String bodyPreview;
    private final NotificationSeverity severity;
    private final NotificationPriority priority;
    private final String actionType;
    private final String actionUrl;
    private final String dedupKey;
    private final boolean mandatory;
    private NotificationItemStatus status;
    private Instant readAt;
    private Instant dismissedAt;
    private final String traceId;
    private final Instant createdAt;
    private Instant updatedAt;

    private NotificationItem(UUID id, UUID recipientUserId, UUID eventDefinitionId,
                             String sourceSystem, String sourceResourceType, UUID sourceResourceId,
                             UUID organizationId, UUID workspaceId, UUID projectId,
                             String title, String bodyPreview,
                             NotificationSeverity severity, NotificationPriority priority,
                             String actionType, String actionUrl, String dedupKey, boolean mandatory,
                             NotificationItemStatus status, Instant readAt, Instant dismissedAt,
                             String traceId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.recipientUserId = recipientUserId;
        this.eventDefinitionId = eventDefinitionId;
        this.sourceSystem = sourceSystem;
        this.sourceResourceType = sourceResourceType;
        this.sourceResourceId = sourceResourceId;
        this.organizationId = organizationId;
        this.workspaceId = workspaceId;
        this.projectId = projectId;
        this.title = title;
        this.bodyPreview = bodyPreview;
        this.severity = severity;
        this.priority = priority;
        this.actionType = actionType;
        this.actionUrl = actionUrl;
        this.dedupKey = dedupKey;
        this.mandatory = mandatory;
        this.status = status;
        this.readAt = readAt;
        this.dismissedAt = dismissedAt;
        this.traceId = traceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static NotificationItem create(UUID recipientUserId, UUID eventDefinitionId,
                                          String sourceSystem, String sourceResourceType, UUID sourceResourceId,
                                          UUID organizationId, UUID workspaceId, UUID projectId,
                                          String title, String bodyPreview,
                                          NotificationSeverity severity, NotificationPriority priority,
                                          String actionType, String actionUrl, String dedupKey,
                                          boolean mandatory, String traceId) {
        if (recipientUserId == null) throw new IllegalArgumentException("recipientUserId required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
        if (dedupKey == null || dedupKey.isBlank()) throw new IllegalArgumentException("dedupKey required");
        Instant now = Instant.now();
        return new NotificationItem(UUID.randomUUID(), recipientUserId, eventDefinitionId,
                sourceSystem, sourceResourceType, sourceResourceId,
                organizationId, workspaceId, projectId,
                title.trim(), bodyPreview,
                severity == null ? NotificationSeverity.INFO : severity,
                priority == null ? NotificationPriority.NORMAL : priority,
                actionType, actionUrl, dedupKey.trim(), mandatory,
                NotificationItemStatus.UNREAD, null, null, traceId, now, now);
    }

    public static NotificationItem reconstitute(UUID id, UUID recipientUserId, UUID eventDefinitionId,
                                                String sourceSystem, String sourceResourceType, UUID sourceResourceId,
                                                UUID organizationId, UUID workspaceId, UUID projectId,
                                                String title, String bodyPreview,
                                                NotificationSeverity severity, NotificationPriority priority,
                                                String actionType, String actionUrl, String dedupKey,
                                                boolean mandatory, NotificationItemStatus status,
                                                Instant readAt, Instant dismissedAt, String traceId,
                                                Instant createdAt, Instant updatedAt) {
        return new NotificationItem(id, recipientUserId, eventDefinitionId, sourceSystem, sourceResourceType,
                sourceResourceId, organizationId, workspaceId, projectId, title, bodyPreview,
                severity, priority, actionType, actionUrl, dedupKey, mandatory, status,
                readAt, dismissedAt, traceId, createdAt, updatedAt);
    }

    public void markRead() {
        if (this.status == NotificationItemStatus.DISMISSED || this.status == NotificationItemStatus.ARCHIVED) {
            return;
        }
        this.status = NotificationItemStatus.READ;
        this.readAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void dismiss() {
        this.status = NotificationItemStatus.DISMISSED;
        this.dismissedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID recipientUserId() { return recipientUserId; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public String sourceSystem() { return sourceSystem; }
    public String sourceResourceType() { return sourceResourceType; }
    public UUID sourceResourceId() { return sourceResourceId; }
    public UUID organizationId() { return organizationId; }
    public UUID workspaceId() { return workspaceId; }
    public UUID projectId() { return projectId; }
    public String title() { return title; }
    public String bodyPreview() { return bodyPreview; }
    public NotificationSeverity severity() { return severity; }
    public NotificationPriority priority() { return priority; }
    public String actionType() { return actionType; }
    public String actionUrl() { return actionUrl; }
    public String dedupKey() { return dedupKey; }
    public boolean mandatory() { return mandatory; }
    public NotificationItemStatus status() { return status; }
    public Instant readAt() { return readAt; }
    public Instant dismissedAt() { return dismissedAt; }
    public String traceId() { return traceId; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
