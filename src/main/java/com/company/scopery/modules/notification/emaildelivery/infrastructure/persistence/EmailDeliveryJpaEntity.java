package com.company.scopery.modules.notification.emaildelivery.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.shared.NotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = NotificationTableNames.EMAIL_DELIVERY)
@Getter
@Setter
@NoArgsConstructor
public class EmailDeliveryJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @Column(name = "template_version_id", nullable = false)
    private UUID templateVersionId;

    @Column(name = "event_definition_id", nullable = false)
    private UUID eventDefinitionId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "recipient_email", length = 254)
    private String toEmail;

    @Column(name = "subject_rendered", columnDefinition = "TEXT")
    private String renderedSubject;

    @Column(name = "html_body_rendered", columnDefinition = "TEXT")
    private String renderedHtmlBody;

    @Column(name = "text_body_rendered", columnDefinition = "TEXT")
    private String renderedTextBody;

    @Column(name = "payload_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String eventPayloadJson;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
}
