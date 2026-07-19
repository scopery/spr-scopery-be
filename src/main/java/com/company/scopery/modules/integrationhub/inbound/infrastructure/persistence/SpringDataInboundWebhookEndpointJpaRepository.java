package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataInboundWebhookEndpointJpaRepository extends JpaRepository<InboundWebhookEndpointJpaEntity, UUID> {
    Optional<InboundWebhookEndpointJpaEntity> findByEndpointCode(String endpointCode);
    List<InboundWebhookEndpointJpaEntity> findByWorkspaceId(UUID workspaceId);
}
