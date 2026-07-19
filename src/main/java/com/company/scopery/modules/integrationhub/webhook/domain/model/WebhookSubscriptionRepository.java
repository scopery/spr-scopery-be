package com.company.scopery.modules.integrationhub.webhook.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface WebhookSubscriptionRepository {
    WebhookSubscription save(WebhookSubscription s);
    Optional<WebhookSubscription> findById(UUID id);
    List<WebhookSubscription> findByWorkspaceId(UUID workspaceId);
}
