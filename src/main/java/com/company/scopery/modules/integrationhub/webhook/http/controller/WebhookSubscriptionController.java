package com.company.scopery.modules.integrationhub.webhook.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import com.company.scopery.modules.integrationhub.webhook.application.action.*;
import com.company.scopery.modules.integrationhub.webhook.application.response.WebhookSubscriptionResponse;
import com.company.scopery.modules.integrationhub.webhook.application.service.WebhookSubscriptionQueryService;
import com.company.scopery.modules.integrationhub.webhook.http.request.CreateWebhookSubscriptionRequest;
import com.company.scopery.modules.integrationhub.webhook.http.request.UpdateWebhookSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Webhook Subscriptions")
public class WebhookSubscriptionController {
    private final WebhookSubscriptionQueryService query;
    private final CreateWebhookSubscriptionAction create;
    private final UpdateWebhookSubscriptionAction update;
    private final EnableWebhookSubscriptionAction enable;
    private final DisableWebhookSubscriptionAction disable;
    private final ArchiveWebhookSubscriptionAction archive;
    public WebhookSubscriptionController(WebhookSubscriptionQueryService query,
            CreateWebhookSubscriptionAction create, UpdateWebhookSubscriptionAction update,
            EnableWebhookSubscriptionAction enable, DisableWebhookSubscriptionAction disable,
            ArchiveWebhookSubscriptionAction archive) {
        this.query = query; this.create = create; this.update = update;
        this.enable = enable; this.disable = disable; this.archive = archive;
    }
    @PostMapping(IntegrationApiPaths.WEBHOOKS) @Operation(summary = "Create webhook subscription")
    public ApiResponse<WebhookSubscriptionResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateWebhookSubscriptionRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.name(), r.endpointUrl(), r.eventTypesJson(), r.payloadVersion(), r.connectionId()));
    }
    @GetMapping(IntegrationApiPaths.WEBHOOKS) @Operation(summary = "List webhook subscriptions")
    public ApiResponse<List<WebhookSubscriptionResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.WEBHOOK_BY_ID) @Operation(summary = "Get webhook subscription by id")
    public ApiResponse<WebhookSubscriptionResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(query.getById(workspaceId, subscriptionId));
    }
    @PutMapping(IntegrationApiPaths.WEBHOOK_BY_ID) @Operation(summary = "Update webhook subscription")
    public ApiResponse<WebhookSubscriptionResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId,
            @Valid @RequestBody UpdateWebhookSubscriptionRequest r) {
        return ApiResponse.success(update.execute(workspaceId, subscriptionId, r.name(), r.endpointUrl(), r.eventTypesJson(), r.payloadVersion()));
    }
    @PostMapping(IntegrationApiPaths.WEBHOOK_ENABLE) @Operation(summary = "Enable webhook subscription")
    public ApiResponse<WebhookSubscriptionResponse> enable(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(enable.execute(workspaceId, subscriptionId));
    }
    @PostMapping(IntegrationApiPaths.WEBHOOK_DISABLE) @Operation(summary = "Disable webhook subscription")
    public ApiResponse<WebhookSubscriptionResponse> disable(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(disable.execute(workspaceId, subscriptionId));
    }
    @PatchMapping(IntegrationApiPaths.WEBHOOK_ARCHIVE) @Operation(summary = "Archive webhook subscription")
    public ApiResponse<WebhookSubscriptionResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(archive.execute(workspaceId, subscriptionId));
    }
}
