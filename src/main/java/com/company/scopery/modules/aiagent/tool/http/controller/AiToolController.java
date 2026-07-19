package com.company.scopery.modules.aiagent.tool.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import com.company.scopery.modules.aiagent.tool.application.action.*;
import com.company.scopery.modules.aiagent.tool.application.command.*;
import com.company.scopery.modules.aiagent.tool.application.query.GetAiToolDetailQuery;
import com.company.scopery.modules.aiagent.tool.application.query.SearchAiToolQuery;
import com.company.scopery.modules.aiagent.tool.application.response.*;
import com.company.scopery.modules.aiagent.tool.application.service.AiToolQueryService;
import com.company.scopery.modules.aiagent.tool.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "AI Agent - Tools", description = "Governed AiTool registry (AIG-012): catalog, permissions, bindings, execution log")
@RestController
@RequestMapping(AiAgentApiPaths.TOOLS)
public class AiToolController {

    private final AiToolQueryService queryService;
    private final CreateAiToolAction createAction;
    private final UpdateAiToolAction updateAction;
    private final ActivateAiToolAction activateAction;
    private final DeactivateAiToolAction deactivateAction;
    private final AddAiToolPermissionAction addPermissionAction;
    private final RemoveAiToolPermissionAction removePermissionAction;
    private final BindAgentToolAction bindAction;
    private final UnbindAgentToolAction unbindAction;
    private final ExecuteAiToolAction executeAction;

    public AiToolController(
            AiToolQueryService queryService,
            CreateAiToolAction createAction,
            UpdateAiToolAction updateAction,
            ActivateAiToolAction activateAction,
            DeactivateAiToolAction deactivateAction,
            AddAiToolPermissionAction addPermissionAction,
            RemoveAiToolPermissionAction removePermissionAction,
            BindAgentToolAction bindAction,
            UnbindAgentToolAction unbindAction,
            ExecuteAiToolAction executeAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.addPermissionAction = addPermissionAction;
        this.removePermissionAction = removePermissionAction;
        this.bindAction = bindAction;
        this.unbindAction = unbindAction;
        this.executeAction = executeAction;
    }

    @Operation(summary = "Register a new AI tool")
    @PostMapping
    public ResponseEntity<ApiResponse<AiToolResponse>> create(@Valid @RequestBody CreateAiToolRequest request) {
        CreateAiToolCommand command = new CreateAiToolCommand(
                request.code(), request.name(), request.description(), request.category(),
                request.mutationType(), Boolean.TRUE.equals(request.requiresHumanApproval()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Update an AI tool")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AiToolDetailResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateAiToolRequest request) {
        UpdateAiToolCommand command = new UpdateAiToolCommand(
                id, request.name(), request.description(), request.category(),
                request.mutationType(), Boolean.TRUE.equals(request.requiresHumanApproval()));
        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(command)));
    }

    @Operation(summary = "Get AI tool detail")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AiToolDetailResponse>> getDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getDetail(new GetAiToolDetailQuery(id))));
    }

    @Operation(summary = "Search AI tools")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AiToolResponse>>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<AiToolResponse> result = queryService.search(new SearchAiToolQuery(category, status, q, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate an AI tool")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AiToolDetailResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(new ActivateAiToolCommand(id))));
    }

    @Operation(summary = "Deactivate an AI tool")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AiToolDetailResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(new DeactivateAiToolCommand(id))));
    }

    @Operation(summary = "Add required business permission for a tool")
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<AiToolPermissionResponse>> addPermission(
            @PathVariable UUID id, @Valid @RequestBody AddAiToolPermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                addPermissionAction.execute(new AddAiToolPermissionCommand(id, request.permissionCode(), request.description()))));
    }

    @Operation(summary = "Remove a tool permission binding")
    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> removePermission(
            @PathVariable UUID id, @PathVariable UUID permissionId) {
        removePermissionAction.execute(new RemoveAiToolPermissionCommand(id, permissionId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Bind an agent to a tool")
    @PostMapping("/{id}/bindings")
    public ResponseEntity<ApiResponse<AiAgentToolBindingResponse>> bind(
            @PathVariable UUID id, @Valid @RequestBody BindAgentToolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                bindAction.execute(new BindAgentToolCommand(id, request.agentId()))));
    }

    @Operation(summary = "List agent bindings for a tool")
    @GetMapping("/{id}/bindings")
    public ResponseEntity<ApiResponse<List<AiAgentToolBindingResponse>>> listBindings(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.listBindings(id)));
    }

    @Operation(summary = "Deactivate an agent-tool binding")
    @DeleteMapping("/{id}/bindings/{agentId}")
    public ResponseEntity<ApiResponse<AiAgentToolBindingResponse>> unbind(
            @PathVariable UUID id, @PathVariable UUID agentId) {
        return ResponseEntity.ok(ApiResponse.success(
                unbindAction.execute(new UnbindAgentToolCommand(id, agentId))));
    }

    @Operation(summary = "Execute (stub/no-op) a registered tool and write AiToolExecution log")
    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<AiToolExecutionResponse>> execute(
            @PathVariable UUID id, @RequestBody(required = false) ExecuteAiToolRequest request) {
        ExecuteAiToolRequest body = request != null ? request : new ExecuteAiToolRequest(null, null);
        return ResponseEntity.ok(ApiResponse.success(
                executeAction.execute(new ExecuteAiToolCommand(id, body.agentId(), body.inputSummary(), null, null, null, null, null))));
    }
}
