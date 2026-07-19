package com.company.scopery.modules.integrationhub.webhook.application.service;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookDeliveryAttemptResponse;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookSubscriptionResponse;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttemptRepository;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class WebhookSubscriptionQueryService {
    private final WebhookSubscriptionRepository subs;
    private final WebhookDeliveryAttemptRepository deliveries;
    private final IntegrationAuthorizationService auth;
    public WebhookSubscriptionQueryService(WebhookSubscriptionRepository subs, WebhookDeliveryAttemptRepository deliveries,
            IntegrationAuthorizationService auth) {
        this.subs = subs; this.deliveries = deliveries; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<WebhookSubscriptionResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return subs.findByWorkspaceId(workspaceId).stream().map(WebhookSubscriptionResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public WebhookSubscriptionResponse getById(UUID workspaceId, UUID subId) {
        auth.requireView(workspaceId);
        var s = subs.findById(subId).orElseThrow(() -> IntegrationExceptions.webhookSubscriptionNotFound(subId));
        if (!workspaceId.equals(s.workspaceId())) throw IntegrationExceptions.webhookSubscriptionNotFound(subId);
        return WebhookSubscriptionResponse.from(s);
    }
    @Transactional(readOnly = true)
    public List<WebhookDeliveryAttemptResponse> listDeliveryAttempts(UUID workspaceId) {
        auth.requireView(workspaceId);
        return deliveries.findByWorkspaceId(workspaceId).stream().map(WebhookDeliveryAttemptResponse::from).toList();
    }
}
