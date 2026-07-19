package com.company.scopery.modules.integrationhub.webhook.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.webhook.application.action.RecordWebhookDeliveryAction;
import com.company.scopery.modules.integrationhub.webhook.application.action.RetryWebhookDeliveryAction;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookDeliveryAttemptResponse;
import com.company.scopery.modules.integrationhub.webhook.application.service.WebhookSubscriptionQueryService;
import com.company.scopery.modules.integrationhub.webhook.http.request.RecordWebhookDeliveryRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Webhook Deliveries")
public class WebhookDeliveryController {
    private final WebhookSubscriptionQueryService query;
    private final RecordWebhookDeliveryAction record;
    private final RetryWebhookDeliveryAction retry;
    public WebhookDeliveryController(WebhookSubscriptionQueryService query, RecordWebhookDeliveryAction record,
            RetryWebhookDeliveryAction retry) {
        this.query = query; this.record = record; this.retry = retry;
    }
    @PostMapping(IntegrationApiPaths.WEBHOOK_DELIVERIES) @Operation(summary = "Record webhook delivery attempt")
    public ApiResponse<WebhookDeliveryAttemptResponse> record(@PathVariable UUID workspaceId, @Valid @RequestBody RecordWebhookDeliveryRequest r) {
        int attempt = r.attemptNumber() == null ? 1 : r.attemptNumber();
        boolean success = r.success() == null || r.success();
        return ApiResponse.success(record.execute(workspaceId, r.webhookSubscriptionId(), r.eventType(), attempt, success, r.responseBody()));
    }
    @GetMapping(IntegrationApiPaths.WEBHOOK_DELIVERIES) @Operation(summary = "List webhook delivery attempts")
    public ApiResponse<List<WebhookDeliveryAttemptResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listDeliveryAttempts(workspaceId));
    }
    @PostMapping(IntegrationApiPaths.WEBHOOK_DELIVERY_RETRY) @Operation(summary = "Retry webhook delivery attempt")
    public ApiResponse<WebhookDeliveryAttemptResponse> retry(@PathVariable UUID workspaceId, @PathVariable UUID attemptId) {
        return ApiResponse.success(retry.execute(workspaceId, attemptId));
    }
}
