package com.company.scopery.modules.integrationhub.webhook.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookSubscriptionResponse;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscriptionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpdateWebhookSubscriptionAction {
    private final WebhookSubscriptionRepository subs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public UpdateWebhookSubscriptionAction(WebhookSubscriptionRepository subs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.subs = subs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public WebhookSubscriptionResponse execute(UUID workspaceId, UUID subId, String name, String endpointUrl,
            String eventTypesJson, String payloadVersion) {
        auth.requireManage(workspaceId);
        var s = subs.findById(subId).orElseThrow(() -> IntegrationExceptions.webhookSubscriptionNotFound(subId));
        if (!workspaceId.equals(s.workspaceId())) throw IntegrationExceptions.webhookSubscriptionNotFound(subId);
        var saved = subs.save(s.update(name, endpointUrl, eventTypesJson, payloadVersion));
        activity.logSuccess("INTEGRATION_WEBHOOK_SUB", saved.id(), "INTEGRATION_WEBHOOK_SUB_UPDATED", "Webhook subscription updated");
        return WebhookSubscriptionResponse.from(saved);
    }
}
