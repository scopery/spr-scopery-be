package com.company.scopery.modules.integrationhub.webhook.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookSubscriptionResponse;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscription;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscriptionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateWebhookSubscriptionAction {
    private final WebhookSubscriptionRepository subs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateWebhookSubscriptionAction(WebhookSubscriptionRepository subs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.subs = subs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public WebhookSubscriptionResponse execute(UUID workspaceId, String name, String endpointUrl,
            String eventTypesJson, String payloadVersion, UUID connectionId) {
        auth.requireManage(workspaceId);
        var saved = subs.save(WebhookSubscription.create(workspaceId, connectionId, name, endpointUrl, eventTypesJson, payloadVersion));
        activity.logSuccess("INTEGRATION_WEBHOOK_SUB", saved.id(), "INTEGRATION_WEBHOOK_SUB_CREATED", "Webhook subscription created");
        return WebhookSubscriptionResponse.from(saved);
    }
}
