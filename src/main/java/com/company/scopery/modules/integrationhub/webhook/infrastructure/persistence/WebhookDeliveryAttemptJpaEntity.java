package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = IntegrationTableNames.WEBHOOK_DELIVERY)
@Getter
@Setter
@NoArgsConstructor
public class WebhookDeliveryAttemptJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "webhook_subscription_id", nullable = false) private UUID webhookSubscriptionId;
    @Column(name = "event_type", nullable = false) private String eventType;
    @Column(nullable = false) private String status;
    @Column(name = "attempt_number", nullable = false) private Integer attemptNumber;
    @Column(name = "response_body_redacted", columnDefinition = "text") private String responseBodyRedacted;
    @Column(name = "sent_at") private Instant sentAt;
    @Version private Integer version;
}
