package com.company.scopery.modules.integrationhub.inbound.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.inbound.application.action.CreateInboundWebhookEndpointAction;
import com.company.scopery.modules.integrationhub.inbound.application.response.InboundWebhookEndpointResponse;
import com.company.scopery.modules.integrationhub.inbound.application.response.InboundWebhookEventResponse;
import com.company.scopery.modules.integrationhub.inbound.application.service.InboundWebhookQueryService;
import com.company.scopery.modules.integrationhub.inbound.http.request.CreateInboundWebhookEndpointRequest;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Inbound Webhooks")
public class InboundWebhookController {
    private final InboundWebhookQueryService query;
    private final CreateInboundWebhookEndpointAction create;
    public InboundWebhookController(InboundWebhookQueryService query, CreateInboundWebhookEndpointAction create) {
        this.query = query; this.create = create;
    }
    @PostMapping(IntegrationApiPaths.INBOUND) @Operation(summary = "Receive inbound webhook (public endpoint)")
    public ApiResponse<Map<String, Object>> receive(@PathVariable String endpointCode, @RequestBody(required = false) String payload) {
        return ApiResponse.success(Map.of("endpointCode", endpointCode, "status", "RECEIVED",
                "note", "Signature verification and payload processing to be implemented"));
    }
    @PostMapping(IntegrationApiPaths.INBOUND_ENDPOINTS) @Operation(summary = "Create inbound webhook endpoint")
    public ApiResponse<InboundWebhookEndpointResponse> createEndpoint(@PathVariable UUID workspaceId,
            @Valid @RequestBody CreateInboundWebhookEndpointRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r.connectionId(), r.endpointCode(), r.providerCode()));
    }
    @GetMapping(IntegrationApiPaths.INBOUND_ENDPOINTS) @Operation(summary = "List inbound webhook endpoints")
    public ApiResponse<List<InboundWebhookEndpointResponse>> listEndpoints(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listEndpoints(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.INBOUND_EVENTS) @Operation(summary = "List inbound webhook events")
    public ApiResponse<List<InboundWebhookEventResponse>> listEvents(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listEvents(workspaceId));
    }
}
