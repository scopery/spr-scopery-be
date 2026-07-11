package com.company.scopery.modules.aiagent.execution.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.execution.application.action.CancelExecutionAction;
import com.company.scopery.modules.aiagent.execution.application.action.CreateExecutionLogAction;
import com.company.scopery.modules.aiagent.execution.application.action.MarkExecutionFailedAction;
import com.company.scopery.modules.aiagent.execution.application.action.MarkExecutionRunningAction;
import com.company.scopery.modules.aiagent.execution.application.action.MarkExecutionSucceededAction;
import com.company.scopery.modules.aiagent.execution.application.command.*;
import com.company.scopery.modules.aiagent.execution.application.query.GetExecutionLogDetailQuery;
import com.company.scopery.modules.aiagent.execution.application.query.SearchExecutionLogQuery;
import com.company.scopery.modules.aiagent.execution.application.response.*;
import com.company.scopery.modules.aiagent.execution.application.service.ExecutionLogQueryService;
import com.company.scopery.modules.aiagent.execution.http.request.CreateExecutionLogRequest;
import com.company.scopery.modules.aiagent.execution.http.request.UpdateExecutionLogResultRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Tag(name = "AI Agent - Execution Logs", description = "Track and manage AI execution records")
@RestController
@RequestMapping(AiAgentApiPaths.EXECUTION_LOGS)
public class ExecutionLogController {

    private final CreateExecutionLogAction createAction;
    private final MarkExecutionRunningAction markRunningAction;
    private final MarkExecutionSucceededAction markSucceededAction;
    private final MarkExecutionFailedAction markFailedAction;
    private final CancelExecutionAction cancelAction;
    private final ExecutionLogQueryService queryService;

    public ExecutionLogController(CreateExecutionLogAction createAction,
                                   MarkExecutionRunningAction markRunningAction,
                                   MarkExecutionSucceededAction markSucceededAction,
                                   MarkExecutionFailedAction markFailedAction,
                                   CancelExecutionAction cancelAction,
                                   ExecutionLogQueryService queryService) {
        this.createAction = createAction;
        this.markRunningAction = markRunningAction;
        this.markSucceededAction = markSucceededAction;
        this.markFailedAction = markFailedAction;
        this.cancelAction = cancelAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new execution log")
    @PostMapping
    public ResponseEntity<ApiResponse<ExecutionLogResponse>> createExecutionLog(
            @Valid @RequestBody CreateExecutionLogRequest request) {
        CreateExecutionLogCommand command = new CreateExecutionLogCommand(
                request.requestId(), request.eventConfigId(), request.eventDefinitionId(),
                request.agentId(), request.promptVersionId(), request.modelDeploymentId(),
                request.triggerSource(), request.metadata());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Mark execution as running")
    @PatchMapping("/{id}/running")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> markRunning(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                markRunningAction.execute(new MarkExecutionRunningCommand(id))));
    }

    @Operation(summary = "Mark execution as succeeded")
    @PatchMapping("/{id}/succeeded")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> markSucceeded(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExecutionLogResultRequest request) {
        MarkExecutionSucceededCommand command = new MarkExecutionSucceededCommand(
                id, request.inputTokenCount(), request.outputTokenCount(),
                request.totalTokenCount(), request.estimatedCost(),
                request.providerRequestId(), request.metadata());
        return ResponseEntity.ok(ApiResponse.success(markSucceededAction.execute(command)));
    }

    @Operation(summary = "Mark execution as failed")
    @PatchMapping("/{id}/failed")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> markFailed(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExecutionLogResultRequest request) {
        MarkExecutionFailedCommand command = new MarkExecutionFailedCommand(
                id, request.errorCode(), request.errorMessage(),
                request.inputTokenCount(), request.outputTokenCount(),
                request.totalTokenCount(), request.estimatedCost(),
                request.providerRequestId(), request.metadata());
        return ResponseEntity.ok(ApiResponse.success(markFailedAction.execute(command)));
    }

    @Operation(summary = "Cancel an execution")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> cancelExecution(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                cancelAction.execute(new CancelExecutionCommand(id))));
    }

    @Operation(summary = "Get execution log detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> getExecutionLogDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                queryService.getExecutionLogDetail(new GetExecutionLogDetailQuery(id))));
    }

    @Operation(summary = "Search and list execution logs with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ExecutionLogResponse>>> searchExecutionLogs(
            @Parameter(description = "Filter by requestId (partial, case-insensitive)")
                @RequestParam(required = false) String requestId,
            @RequestParam(required = false) UUID eventConfigId,
            @RequestParam(required = false) UUID eventDefinitionId,
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) UUID promptVersionId,
            @RequestParam(required = false) UUID modelDeploymentId,
            @Parameter(description = "Filter by trigger source (EVENT, MANUAL, API, PLAYGROUND, SCHEDULED)")
                @RequestParam(required = false) String triggerSource,
            @Parameter(description = "Filter by status (PENDING, RUNNING, SUCCEEDED, FAILED, CANCELLED)")
                @RequestParam(required = false) String status,
            @Parameter(description = "Filter createdAt >= this value (ISO-8601)")
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @Parameter(description = "Filter createdAt <= this value (ISO-8601)")
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchExecutionLogQuery query = new SearchExecutionLogQuery(
                requestId, eventConfigId, eventDefinitionId, agentId, promptVersionId,
                modelDeploymentId, triggerSource, status, createdFrom, createdTo, page, size);

        PageResult<ExecutionLogResponse> result = queryService.searchExecutionLogs(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
