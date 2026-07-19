package com.company.scopery.modules.integrationhub.inbound.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEvent;
import com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence.InboundWebhookEventJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class InboundWebhookEventPersistenceMapper {
    public InboundWebhookEventJpaEntity toJpaEntity(InboundWebhookEvent d) {
        InboundWebhookEventJpaEntity e = new InboundWebhookEventJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setInboundEndpointId(d.inboundEndpointId());
        e.setProviderCode(d.providerCode()); e.setExternalEventId(d.externalEventId());
        e.setEventType(d.eventType()); e.setStatus(d.status()); e.setPayloadReference(d.payloadReference());
        e.setPayloadRedactedJson(d.payloadRedactedJson());
        e.setReceivedAt(d.receivedAt()); e.setProcessedAt(d.processedAt());
        e.setFailureCode(d.failureCode()); e.setFailureMessage(d.failureMessage());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public InboundWebhookEvent toDomain(InboundWebhookEventJpaEntity e) {
        return new InboundWebhookEvent(e.getId(), e.getWorkspaceId(), e.getInboundEndpointId(), e.getProviderCode(),
                e.getExternalEventId(), e.getEventType(), e.getStatus(), e.getPayloadReference(), e.getPayloadRedactedJson(),
                e.getReceivedAt(), e.getProcessedAt(), e.getFailureCode(), e.getFailureMessage(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
