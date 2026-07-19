package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataWebhookDeliveryAttemptJpaRepository extends JpaRepository<WebhookDeliveryAttemptJpaEntity, UUID> {
    List<WebhookDeliveryAttemptJpaEntity> findByWorkspaceId(UUID workspaceId);
}
