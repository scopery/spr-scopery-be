package com.company.scopery.modules.eventregistry.eventdefinition.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.ActivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.CreateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.DeactivateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.DeprecateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.UpdateEventDefinitionAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.action.UpsertEventVariablesAction;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.ActivateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.CreateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeactivateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeprecateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpdateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpsertEventVariablesCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.GetEventDefinitionDetailQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.SearchEventDefinitionQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.service.EventDefinitionQueryService;
import com.company.scopery.modules.eventregistry.eventdefinition.http.request.CreateEventDefinitionRequest;
import com.company.scopery.modules.eventregistry.eventdefinition.http.request.DeprecateEventDefinitionRequest;
import com.company.scopery.modules.eventregistry.eventdefinition.http.request.UpdateEventDefinitionRequest;
import com.company.scopery.modules.eventregistry.eventdefinition.http.request.UpsertEventVariablesRequest;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Event Registry - Event Definitions", description = "Manage reusable business event definitions")
@RestController
@RequestMapping(EventRegistryApiPaths.EVENT_DEFINITIONS)
public class EventDefinitionController {

    private final CreateEventDefinitionAction createAction;
    private final UpdateEventDefinitionAction updateAction;
    private final ActivateEventDefinitionAction activateAction;
    private final DeactivateEventDefinitionAction deactivateAction;
    private final DeprecateEventDefinitionAction deprecateAction;
    private final UpsertEventVariablesAction upsertVariablesAction;
    private final EventDefinitionQueryService queryService;

    public EventDefinitionController(CreateEventDefinitionAction createAction,
                                     UpdateEventDefinitionAction updateAction,
                                     ActivateEventDefinitionAction activateAction,
                                     DeactivateEventDefinitionAction deactivateAction,
                                     DeprecateEventDefinitionAction deprecateAction,
                                     UpsertEventVariablesAction upsertVariablesAction,
                                     EventDefinitionQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.deprecateAction = deprecateAction;
        this.upsertVariablesAction = upsertVariablesAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new event definition")
    @PostMapping
    public ResponseEntity<ApiResponse<EventDefinitionResponse>> createEventDefinition(
            @Valid @RequestBody CreateEventDefinitionRequest request) {

        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                request.code(), request.name(), request.sourceSystem(), request.eventKey(),
                request.description(), request.inputSchema(), request.outputSchema(),
                request.dataClassification(), request.ownerModule());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Update an existing event definition")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> updateEventDefinition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventDefinitionRequest request) {

        UpdateEventDefinitionCommand command = new UpdateEventDefinitionCommand(
                id, request.name(), request.description(), request.inputSchema(), request.outputSchema(),
                request.dataClassification(), request.ownerModule());

        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(command)));
    }

    @Operation(summary = "Get event definition detail by ID (includes variables)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> getEventDefinitionDetail(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                queryService.getEventDefinitionDetail(new GetEventDefinitionDetailQuery(id))));
    }

    @Operation(summary = "Search and list event definitions with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EventDefinitionResponse>>> searchEventDefinitions(
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by source system (exact match, case-insensitive)")
                @RequestParam(required = false) String sourceSystem,
            @Parameter(description = "Filter by event key (exact match)")
                @RequestParam(required = false) String eventKey,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)")
                @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResult<EventDefinitionResponse> result = queryService.searchEventDefinitions(
                new SearchEventDefinitionQuery(keyword, sourceSystem, eventKey, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate an event definition")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> activateEventDefinition(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                activateAction.execute(new ActivateEventDefinitionCommand(id))));
    }

    @Operation(summary = "Deactivate an event definition")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> deactivateEventDefinition(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                deactivateAction.execute(new DeactivateEventDefinitionCommand(id))));
    }

    @Operation(summary = "Deprecate an event definition")
    @PatchMapping("/{id}/deprecate")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> deprecateEventDefinition(
            @PathVariable UUID id,
            @RequestBody(required = false) DeprecateEventDefinitionRequest request) {

        DeprecateEventDefinitionRequest body = request == null
                ? new DeprecateEventDefinitionRequest(null, null)
                : request;

        return ResponseEntity.ok(ApiResponse.success(
                deprecateAction.execute(new DeprecateEventDefinitionCommand(
                        id, body.replacementEventDefinitionId(), body.reason()))));
    }

    @Operation(summary = "Upsert variables for an event definition (replaces all existing)")
    @PutMapping("/{id}/variables")
    public ResponseEntity<ApiResponse<List<EventVariableResponse>>> upsertEventVariables(
            @PathVariable UUID id,
            @Valid @RequestBody UpsertEventVariablesRequest request) {

        List<UpsertEventVariablesCommand.VariableEntry> entries = request.variables().stream()
                .map(v -> new UpsertEventVariablesCommand.VariableEntry(
                        v.variablePath(), v.variableLabel(), v.variableType(),
                        v.required(), v.sensitive(), v.description(), v.exampleValue()))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                upsertVariablesAction.execute(new UpsertEventVariablesCommand(id, entries))));
    }

    @Operation(summary = "Get all variables for an event definition")
    @GetMapping("/{id}/variables")
    public ResponseEntity<ApiResponse<List<EventVariableResponse>>> getEventVariables(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(queryService.getEventVariables(id)));
    }
}
