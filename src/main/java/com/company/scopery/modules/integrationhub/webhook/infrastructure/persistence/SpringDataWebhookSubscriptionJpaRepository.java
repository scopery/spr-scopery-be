package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataWebhookSubscriptionJpaRepository extends JpaRepository<WebhookSubscriptionJpaEntity, UUID> {
    List<WebhookSubscriptionJpaEntity> findByWorkspaceId(UUID workspaceId);
}
