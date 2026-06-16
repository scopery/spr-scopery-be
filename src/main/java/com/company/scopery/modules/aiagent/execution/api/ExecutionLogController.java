package com.company.scopery.modules.aiagent.execution.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.execution.api.request.CreateExecutionLogRequest;
import com.company.scopery.modules.aiagent.execution.api.request.UpdateExecutionLogResultRequest;
import com.company.scopery.modules.aiagent.execution.application.ExecutionLogApplicationService;
import com.company.scopery.modules.aiagent.execution.application.command.*;
import com.company.scopery.modules.aiagent.execution.application.query.*;
import com.company.scopery.modules.aiagent.execution.application.response.*;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

    private final ExecutionLogApplicationService service;

    public ExecutionLogController(ExecutionLogApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new execution log")
    @PostMapping
    public ResponseEntity<ApiResponse<ExecutionLogResponse>> createExecutionLog(
            @Valid @RequestBody CreateExecutionLogRequest request) {

        CreateExecutionLogCommand command = new CreateExecutionLogCommand(
                request.requestId(), request.eventConfigId(), request.eventDefinitionId(),
                request.agentId(), request.promptVersionId(), request.modelDeploymentId(),
                request.triggerSource(), request.metadata());

        ExecutionLogResponse response = service.createExecutionLog(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Mark execution as running")
    @PatchMapping("/{id}/running")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> markRunning(
            @PathVariable UUID id) {

        ExecutionLogDetailResponse response = service.markRunning(new MarkExecutionRunningCommand(id));
        return ResponseEntity.ok(ApiResponse.success(response));
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

        ExecutionLogDetailResponse response = service.markSucceeded(command);
        return ResponseEntity.ok(ApiResponse.success(response));
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

        ExecutionLogDetailResponse response = service.markFailed(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Cancel an execution")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> cancelExecution(
            @PathVariable UUID id) {

        ExecutionLogDetailResponse response = service.cancelExecution(new CancelExecutionCommand(id));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get execution log detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExecutionLogDetailResponse>> getExecutionLogDetail(
            @PathVariable UUID id) {

        ExecutionLogDetailResponse response = service.getExecutionLogDetail(new GetExecutionLogDetailQuery(id));
        return ResponseEntity.ok(ApiResponse.success(response));
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

        Page<ExecutionLogResponse> result = service.searchExecutionLogs(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }
}
