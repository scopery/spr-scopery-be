package com.company.scopery.modules.eventregistry.eventdefinition.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.api.request.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.EventDefinitionApplicationService;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.*;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Event Registry - Event Definitions", description = "Manage reusable business event definitions")
@RestController
@RequestMapping(EventRegistryApiPaths.EVENT_DEFINITIONS)
public class EventDefinitionController {

    private final EventDefinitionApplicationService service;

    public EventDefinitionController(EventDefinitionApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new event definition")
    @PostMapping
    public ResponseEntity<ApiResponse<EventDefinitionResponse>> createEventDefinition(
            @Valid @RequestBody CreateEventDefinitionRequest request) {

        CreateEventDefinitionCommand command = new CreateEventDefinitionCommand(
                request.code(), request.name(), request.sourceSystem(), request.eventKey(),
                request.description(), request.inputSchema(), request.outputSchema());

        EventDefinitionResponse response = service.createEventDefinition(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing event definition")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> updateEventDefinition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventDefinitionRequest request) {

        UpdateEventDefinitionCommand command = new UpdateEventDefinitionCommand(
                id, request.name(), request.description(), request.inputSchema(), request.outputSchema());

        EventDefinitionDetailResponse response = service.updateEventDefinition(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get event definition detail by ID (includes variables)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> getEventDefinitionDetail(
            @PathVariable UUID id) {

        GetEventDefinitionDetailQuery query = new GetEventDefinitionDetailQuery(id);
        EventDefinitionDetailResponse response = service.getEventDefinitionDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list event definitions with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EventDefinitionResponse>>> searchEventDefinitions(
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by source system (exact match, case-insensitive)")
                @RequestParam(required = false) String sourceSystem,
            @Parameter(description = "Filter by event key (exact match, case-insensitive)")
                @RequestParam(required = false) String eventKey,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)")
                @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchEventDefinitionQuery query = new SearchEventDefinitionQuery(
                keyword, sourceSystem, eventKey, status, page, size);

        Page<EventDefinitionResponse> result = service.searchEventDefinitions(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate an event definition")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> activateEventDefinition(
            @PathVariable UUID id) {

        ActivateEventDefinitionCommand command = new ActivateEventDefinitionCommand(id);
        EventDefinitionDetailResponse response = service.activateEventDefinition(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate an event definition")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<EventDefinitionDetailResponse>> deactivateEventDefinition(
            @PathVariable UUID id) {

        DeactivateEventDefinitionCommand command = new DeactivateEventDefinitionCommand(id);
        EventDefinitionDetailResponse response = service.deactivateEventDefinition(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Upsert variables for an event definition (replaces all existing)")
    @PutMapping("/{id}/variables")
    public ResponseEntity<ApiResponse<List<EventVariableResponse>>> upsertEventVariables(
            @PathVariable UUID id,
            @Valid @RequestBody UpsertEventVariablesRequest request) {

        List<UpsertEventVariablesCommand.VariableEntry> entries = request.variables().stream()
                .map(v -> new UpsertEventVariablesCommand.VariableEntry(
                        v.variablePath(), v.variableLabel(), v.variableType(),
                        v.required(), v.description(), v.exampleValue()))
                .toList();

        List<EventVariableResponse> response = service.upsertEventVariables(
                new UpsertEventVariablesCommand(id, entries));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get all variables for an event definition")
    @GetMapping("/{id}/variables")
    public ResponseEntity<ApiResponse<List<EventVariableResponse>>> getEventVariables(
            @PathVariable UUID id) {

        List<EventVariableResponse> response = service.getEventVariables(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
