package com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.shared.NotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = NotificationTableNames.EMAIL_TEMPLATE)
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_code", nullable = false, length = 150)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "scope", nullable = false, length = 20)
    private String scope;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "event_definition_id", nullable = false)
    private UUID eventDefinitionId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "current_version_id")
    private UUID currentVersionId;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
