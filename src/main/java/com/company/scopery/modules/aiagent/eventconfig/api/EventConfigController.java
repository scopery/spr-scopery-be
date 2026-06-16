package com.company.scopery.modules.aiagent.eventconfig.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.eventconfig.api.request.*;
import com.company.scopery.modules.aiagent.eventconfig.application.EventConfigApplicationService;
import com.company.scopery.modules.aiagent.eventconfig.application.command.*;
import com.company.scopery.modules.aiagent.eventconfig.application.query.*;
import com.company.scopery.modules.aiagent.eventconfig.application.response.*;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Event Configurations", description = "Manage AI Agent reactions to registered business events")
@RestController
@RequestMapping(AiAgentApiPaths.EVENT_CONFIGS)
public class EventConfigController {

    private final EventConfigApplicationService eventConfigApplicationService;

    public EventConfigController(EventConfigApplicationService eventConfigApplicationService) {
        this.eventConfigApplicationService = eventConfigApplicationService;
    }

    @Operation(summary = "Create a new event configuration")
    @PostMapping
    public ResponseEntity<ApiResponse<EventConfigResponse>> createEventConfig(
            @Valid @RequestBody CreateEventConfigRequest request) {

        CreateEventConfigCommand command = new CreateEventConfigCommand(
                request.code(), request.name(), request.eventDefinitionId(),
                request.environment(), request.triggerType(), request.agentId(),
                request.promptVersionId(), request.modelDeploymentId(),
                request.conditionExpression(), request.description());

        EventConfigResponse response = eventConfigApplicationService.createEventConfig(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing event configuration")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventConfigDetailResponse>> updateEventConfig(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventConfigRequest request) {

        UpdateEventConfigCommand command = new UpdateEventConfigCommand(
                id, request.name(), request.triggerType(), request.agentId(),
                request.promptVersionId(), request.modelDeploymentId(),
                request.conditionExpression(), request.description());

        EventConfigDetailResponse response = eventConfigApplicationService.updateEventConfig(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Resolve the active event configuration for an event and environment")
    @GetMapping("/resolve")
    public ResponseEntity<ApiResponse<EventConfigDetailResponse>> resolveActiveEventConfig(
            @Parameter(description = "Event definition ID (use this OR sourceSystem+eventKey)")
                @RequestParam(required = false) UUID eventDefinitionId,
            @Parameter(description = "Source system code (used together with eventKey if eventDefinitionId is not provided)")
                @RequestParam(required = false) String sourceSystem,
            @Parameter(description = "Event key (used together with sourceSystem if eventDefinitionId is not provided)")
                @RequestParam(required = false) String eventKey,
            @Parameter(description = "Environment (DEV/UAT/PROD — defaults to runtime environment if blank)")
                @RequestParam(required = false) String environment) {

        ResolveActiveEventConfigQuery query = new ResolveActiveEventConfigQuery(
                eventDefinitionId, sourceSystem, eventKey, environment);
        EventConfigDetailResponse response = eventConfigApplicationService.resolveActiveEventConfig(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get event configuration detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventConfigDetailResponse>> getEventConfigDetail(@PathVariable UUID id) {
        GetEventConfigDetailQuery query = new GetEventConfigDetailQuery(id);
        EventConfigDetailResponse response = eventConfigApplicationService.getEventConfigDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list event configurations with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EventConfigResponse>>> searchEventConfigs(
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by event definition ID")
                @RequestParam(required = false) UUID eventDefinitionId,
            @Parameter(description = "Filter by environment (DEV/UAT/PROD)")
                @RequestParam(required = false) String environment,
            @Parameter(description = "Filter by trigger type (EVENT/MANUAL/SCHEDULED/API)")
                @RequestParam(required = false) String triggerType,
            @Parameter(description = "Filter by status (ACTIVE/INACTIVE/DEPRECATED)")
                @RequestParam(required = false) String status,
            @Parameter(description = "Filter by agent ID")
                @RequestParam(required = false) UUID agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchEventConfigQuery query = new SearchEventConfigQuery(
                keyword, eventDefinitionId, environment, triggerType, status, agentId, page, size);
        Page<EventConfigResponse> result = eventConfigApplicationService.searchEventConfigs(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate an event configuration")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EventConfigDetailResponse>> activateEventConfig(@PathVariable UUID id) {
        ActivateEventConfigCommand command = new ActivateEventConfigCommand(id);
        EventConfigDetailResponse response = eventConfigApplicationService.activateEventConfig(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate an event configuration")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EventConfigDetailResponse>> deactivateEventConfig(@PathVariable UUID id) {
        DeactivateEventConfigCommand command = new DeactivateEventConfigCommand(id);
        EventConfigDetailResponse response = eventConfigApplicationService.deactivateEventConfig(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
