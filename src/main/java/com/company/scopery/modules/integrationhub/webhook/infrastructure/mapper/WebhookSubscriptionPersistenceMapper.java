package com.company.scopery.modules.integrationhub.webhook.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscription;
import com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence.WebhookSubscriptionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class WebhookSubscriptionPersistenceMapper {
    public WebhookSubscriptionJpaEntity toJpaEntity(WebhookSubscription d) {
        WebhookSubscriptionJpaEntity e = new WebhookSubscriptionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setName(d.name()); e.setEndpointUrl(d.endpointUrl()); e.setEventTypesJson(d.eventTypesJson());
        e.setPayloadVersion(d.payloadVersion()); e.setSigningSecretReferenceId(d.signingSecretReferenceId());
        e.setStatus(d.status()); e.setMaxAttempts(d.maxAttempts()); e.setTimeoutSeconds(d.timeoutSeconds());
        e.setDisabledAt(d.disabledAt()); e.setArchivedAt(d.archivedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public WebhookSubscription toDomain(WebhookSubscriptionJpaEntity e) {
        return new WebhookSubscription(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getName(),
                e.getEndpointUrl(), e.getEventTypesJson(), e.getPayloadVersion(), e.getSigningSecretReferenceId(),
                e.getStatus(), e.getMaxAttempts() == null ? 5 : e.getMaxAttempts(),
                e.getTimeoutSeconds() == null ? 10 : e.getTimeoutSeconds(),
                e.getDisabledAt(), e.getArchivedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
