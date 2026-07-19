package com.company.scopery.modules.aiagent.agent.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.agent.application.action.*;
import com.company.scopery.modules.aiagent.agent.application.command.*;
import com.company.scopery.modules.aiagent.agent.application.query.*;
import com.company.scopery.modules.aiagent.agent.application.response.*;
import com.company.scopery.modules.aiagent.agent.application.service.AgentQueryService;
import com.company.scopery.modules.aiagent.agent.http.request.*;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Agents", description = "Manage reusable AI business agent profiles")
@RestController
@RequestMapping(AiAgentApiPaths.AGENTS)
public class AgentController {

    private final AgentQueryService queryService;
    private final CreateAgentAction createAction;
    private final UpdateAgentAction updateAction;
    private final ActivateAgentAction activateAction;
    private final DeactivateAgentAction deactivateAction;

    public AgentController(AgentQueryService queryService,
                           CreateAgentAction createAction,
                           UpdateAgentAction updateAction,
                           ActivateAgentAction activateAction,
                           DeactivateAgentAction deactivateAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
    }

    @Operation(summary = "Create a new agent")
    @PostMapping
    public ResponseEntity<ApiResponse<AgentResponse>> createAgent(
            @Valid @RequestBody CreateAgentRequest request) {

        CreateAgentCommand command = new CreateAgentCommand(
                request.name(), request.code(), request.type(), request.description(),
                request.defaultModelDeploymentId(), request.outputFormat(),
                request.autonomyLevel(), request.scope(),
                request.organizationId(), request.workspaceId());

        AgentResponse response = createAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing agent")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailResponse>> updateAgent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAgentRequest request) {

        UpdateAgentCommand command = new UpdateAgentCommand(
                id, request.name(), request.type(), request.description(),
                request.defaultModelDeploymentId(), request.outputFormat(),
                request.autonomyLevel(), request.scope(),
                request.organizationId(), request.workspaceId());

        AgentDetailResponse response = updateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get agent detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailResponse>> getAgentDetail(@PathVariable UUID id) {
        GetAgentDetailQuery query = new GetAgentDetailQuery(id);
        AgentDetailResponse response = queryService.getAgentDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list agents with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AgentResponse>>> searchAgents(
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by agent type (EXTRACTION, CLASSIFICATION, SUMMARIZATION, GENERATION, VALIDATION, RECOMMENDATION, OTHER)")
                @RequestParam(required = false) String type,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)")
                @RequestParam(required = false) String status,
            @Parameter(description = "Filter by output format (TEXT, JSON, MARKDOWN, HTML, TABLE)")
                @RequestParam(required = false) String outputFormat,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchAgentQuery query = new SearchAgentQuery(keyword, type, status, outputFormat, page, size);
        PageResult<AgentResponse> result = queryService.searchAgents(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate an agent")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AgentDetailResponse>> activateAgent(@PathVariable UUID id) {
        ActivateAgentCommand command = new ActivateAgentCommand(id);
        AgentDetailResponse response = activateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate an agent")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AgentDetailResponse>> deactivateAgent(@PathVariable UUID id) {
        DeactivateAgentCommand command = new DeactivateAgentCommand(id);
        AgentDetailResponse response = deactivateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
