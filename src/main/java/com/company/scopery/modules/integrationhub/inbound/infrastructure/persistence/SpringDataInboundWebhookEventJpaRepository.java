package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataInboundWebhookEventJpaRepository extends JpaRepository<InboundWebhookEventJpaEntity, UUID> {
    Optional<InboundWebhookEventJpaEntity> findByInboundEndpointIdAndExternalEventId(UUID endpointId, String externalEventId);
    List<InboundWebhookEventJpaEntity> findByWorkspaceId(UUID workspaceId);
}
