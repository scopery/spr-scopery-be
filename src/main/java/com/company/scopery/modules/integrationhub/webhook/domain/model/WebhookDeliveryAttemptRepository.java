package com.company.scopery.modules.integrationhub.webhook.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookDeliveryAttemptRepository {
    WebhookDeliveryAttempt save(WebhookDeliveryAttempt attempt);
    Optional<WebhookDeliveryAttempt> findById(UUID id);
    List<WebhookDeliveryAttempt> findByWorkspaceId(UUID workspaceId);
}
