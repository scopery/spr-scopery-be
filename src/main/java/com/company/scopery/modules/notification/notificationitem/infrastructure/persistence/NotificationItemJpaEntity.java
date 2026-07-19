package com.company.scopery.modules.notification.notificationitem.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.shared.NotificationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = NotificationTableNames.NOTIFICATION_ITEM)
@Getter
@Setter
@NoArgsConstructor
public class NotificationItemJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "recipient_user_id", nullable = false)
    private UUID recipientUserId;

    @Column(name = "event_definition_id")
    private UUID eventDefinitionId;

    @Column(name = "source_system", length = 100)
    private String sourceSystem;

    @Column(name = "source_resource_type", length = 100)
    private String sourceResourceType;

    @Column(name = "source_resource_id")
    private UUID sourceResourceId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body_preview", columnDefinition = "TEXT")
    private String bodyPreview;

    @Column(name = "severity", nullable = false, length = 50)
    private String severity;

    @Column(name = "priority", nullable = false, length = 50)
    private String priority;

    @Column(name = "action_type", length = 100)
    private String actionType;

    @Column(name = "action_url", columnDefinition = "TEXT")
    private String actionUrl;

    @Column(name = "dedup_key", nullable = false, length = 255)
    private String dedupKey;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "dismissed_at")
    private Instant dismissedAt;

    @Column(name = "trace_id", length = 100)
    private String traceId;
}
