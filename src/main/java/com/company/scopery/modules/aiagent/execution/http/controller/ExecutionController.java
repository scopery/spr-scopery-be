package com.company.scopery.modules.aiagent.execution.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.execution.application.action.ExecuteEventAction;
import com.company.scopery.modules.aiagent.execution.application.action.ExecuteEventConfigAction;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventCommand;
import com.company.scopery.modules.aiagent.execution.application.command.ExecuteEventConfigCommand;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionRunResponse;
import com.company.scopery.modules.aiagent.execution.http.request.ExecuteEventConfigRequest;
import com.company.scopery.modules.aiagent.execution.http.request.ExecuteEventRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Executions")
@RestController
@RequestMapping(AiAgentApiPaths.EXECUTIONS)
public class ExecutionController {

    private final ExecuteEventAction executeEventAction;
    private final ExecuteEventConfigAction executeEventConfigAction;

    public ExecutionController(ExecuteEventAction executeEventAction,
                                ExecuteEventConfigAction executeEventConfigAction) {
        this.executeEventAction = executeEventAction;
        this.executeEventConfigAction = executeEventConfigAction;
    }

    @Operation(summary = "Trigger AI execution by event (eventDefinitionId, sourceSystem+eventKey, or eventCode)")
    @PostMapping("/event")
    public ResponseEntity<ApiResponse<ExecutionRunResponse>> executeEvent(
            @Valid @RequestBody ExecuteEventRequest request) {
        ExecuteEventCommand command = new ExecuteEventCommand(
                request.requestId(),
                request.eventDefinitionId(),
                request.eventCode(),
                request.sourceSystem(),
                request.eventKey(),
                request.environment(),
                request.triggerSource(),
                request.inputVariables());
        return ResponseEntity.ok(ApiResponse.success(executeEventAction.execute(command)));
    }

    @Operation(summary = "Trigger AI execution by EventConfig ID")
    @PostMapping("/event-config/{eventConfigId}")
    public ResponseEntity<ApiResponse<ExecutionRunResponse>> executeEventConfig(
            @PathVariable UUID eventConfigId,
            @Valid @RequestBody ExecuteEventConfigRequest request) {
        ExecuteEventConfigCommand command = new ExecuteEventConfigCommand(
                request.requestId(),
                eventConfigId,
                request.triggerSource(),
                request.inputVariables());
        return ResponseEntity.ok(ApiResponse.success(executeEventConfigAction.execute(command)));
    }
}
