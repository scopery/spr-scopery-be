package com.company.scopery.modules.notification.emailrule.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.shared.NotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = NotificationTableNames.EMAIL_RULE)
@Getter
@Setter
@NoArgsConstructor
public class EmailRuleJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "rule_code", nullable = false, length = 150)
    private String code;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "scope", nullable = false, length = 20)
    private String scope;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "event_definition_id", nullable = false)
    private UUID eventDefinitionId;

    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @Column(name = "recipient_strategy", nullable = false, length = 50)
    private String recipientStrategy;

    @Column(name = "recipient_config_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String recipientConfigJson;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
