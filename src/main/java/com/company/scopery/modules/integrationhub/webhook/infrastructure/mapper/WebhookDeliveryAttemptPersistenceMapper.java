package com.company.scopery.modules.integrationhub.webhook.infrastructure.mapper;

import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttempt;
import com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence.WebhookDeliveryAttemptJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WebhookDeliveryAttemptPersistenceMapper {
    public WebhookDeliveryAttemptJpaEntity toJpaEntity(WebhookDeliveryAttempt d) {
        WebhookDeliveryAttemptJpaEntity e = new WebhookDeliveryAttemptJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setWebhookSubscriptionId(d.webhookSubscriptionId());
        e.setEventType(d.eventType());
        e.setStatus(d.status());
        e.setAttemptNumber(d.attemptNumber());
        e.setResponseBodyRedacted(d.responseBodyRedacted());
        e.setSentAt(d.sentAt());
        e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public WebhookDeliveryAttempt toDomain(WebhookDeliveryAttemptJpaEntity e) {
        return new WebhookDeliveryAttempt(
                e.getId(), e.getWorkspaceId(), e.getWebhookSubscriptionId(), e.getEventType(), e.getStatus(),
                e.getAttemptNumber() == null ? 0 : e.getAttemptNumber(), e.getResponseBodyRedacted(), e.getSentAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
