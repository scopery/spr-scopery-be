package com.company.scopery.modules.integrationhub.webhook.application.action;

import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEvent;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEventRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookDeliveryAttemptResponse;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttempt;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttemptRepository;
import com.company.scopery.modules.integrationhub.webhook.domain.service.WebhookDeliveryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RecordWebhookDeliveryAction {
    private final WebhookDeliveryAttemptRepository deliveries;
    private final DeadLetterEventRepository deadLetters;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;

    public RecordWebhookDeliveryAction(
            WebhookDeliveryAttemptRepository deliveries,
            DeadLetterEventRepository deadLetters,
            IntegrationAuthorizationService auth,
            IntegrationActivityLogger activity) {
        this.deliveries = deliveries;
        this.deadLetters = deadLetters;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public WebhookDeliveryAttemptResponse execute(UUID workspaceId, UUID subscriptionId, String eventType,
            int attempt, boolean success, String responseBody) {
        auth.requireManage(workspaceId);
        String type = eventType == null ? "GENERIC" : eventType;
        String redacted = WebhookDeliveryService.redactPayload(responseBody);
        var saved = deliveries.save(WebhookDeliveryAttempt.recordAttempt(
                workspaceId, subscriptionId, type, attempt, success, redacted));
        activity.logSuccess("INTEGRATION_WEBHOOK_DELIVERY", saved.id(), "INTEGRATION_WEBHOOK_DELIVERY_RECORDED",
                "Webhook delivery recorded: " + saved.status());
        if ("DEAD_LETTERED".equals(saved.status())) {
            DeadLetterEvent dl = deadLetters.save(DeadLetterEvent.fromWebhookFailure(
                    workspaceId, saved.id(), type, "delivery:" + saved.id(),
                    saved.attemptNumber(),
                    "Webhook delivery exhausted after " + saved.attemptNumber() + " attempts"));
            activity.logSuccess("INTEGRATION_DEAD_LETTER", dl.id(), "INTEGRATION_DEAD_LETTER_CREATED", "Dead letter created from webhook failure");
        }
        return WebhookDeliveryAttemptResponse.from(saved);
    }
}
