package com.company.scopery.modules.integrationhub.inbound.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpoint;
import com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence.InboundWebhookEndpointJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class InboundWebhookEndpointPersistenceMapper {
    public InboundWebhookEndpointJpaEntity toJpaEntity(InboundWebhookEndpoint d) {
        InboundWebhookEndpointJpaEntity e = new InboundWebhookEndpointJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setEndpointCode(d.endpointCode()); e.setProviderCode(d.providerCode());
        e.setSigningSecretReferenceId(d.signingSecretReferenceId());
        e.setStatus(d.status()); e.setDisabledAt(d.disabledAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public InboundWebhookEndpoint toDomain(InboundWebhookEndpointJpaEntity e) {
        return new InboundWebhookEndpoint(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getEndpointCode(),
                e.getProviderCode(), e.getSigningSecretReferenceId(), e.getStatus(), e.getDisabledAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
