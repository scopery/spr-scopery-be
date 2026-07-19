package com.company.scopery.modules.integrationhub.webhook.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookDeliveryAttemptResponse;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttempt;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttemptRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RetryWebhookDeliveryAction {
    private final WebhookDeliveryAttemptRepository deliveries;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public RetryWebhookDeliveryAction(WebhookDeliveryAttemptRepository deliveries, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.deliveries = deliveries; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public WebhookDeliveryAttemptResponse execute(UUID workspaceId, UUID attemptId) {
        auth.requireManage(workspaceId);
        var d = deliveries.findById(attemptId).orElseThrow(() -> IntegrationExceptions.webhookDeliveryNotFound(attemptId));
        if (!workspaceId.equals(d.workspaceId())) throw IntegrationExceptions.webhookDeliveryNotFound(attemptId);
        if ("SENT".equals(d.status())) throw IntegrationExceptions.webhookDeliveryRetryNotAllowed(d.status());
        var next = WebhookDeliveryAttempt.recordAttempt(workspaceId, d.webhookSubscriptionId(), d.eventType(),
                d.attemptNumber() + 1, false, d.responseBodyRedacted());
        var saved = deliveries.save(next);
        activity.logSuccess("INTEGRATION_WEBHOOK_DELIVERY", saved.id(), "INTEGRATION_WEBHOOK_DELIVERY_RETRIED", "Webhook delivery retried");
        return WebhookDeliveryAttemptResponse.from(saved);
    }
}
