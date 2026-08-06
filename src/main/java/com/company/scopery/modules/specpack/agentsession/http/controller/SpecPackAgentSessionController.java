package com.company.scopery.modules.specpack.agentsession.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.agentsession.application.action.AdvanceStageAction;
import com.company.scopery.modules.specpack.agentsession.application.action.CancelSessionAction;
import com.company.scopery.modules.specpack.agentsession.application.action.StartAgentSessionAction;
import com.company.scopery.modules.specpack.agentsession.application.action.UpdateReadinessAction;
import com.company.scopery.modules.specpack.agentsession.application.command.AdvanceStageCommand;
import com.company.scopery.modules.specpack.agentsession.application.command.CancelSessionCommand;
import com.company.scopery.modules.specpack.agentsession.application.command.StartAgentSessionCommand;
import com.company.scopery.modules.specpack.agentsession.application.command.UpdateReadinessCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentStageResponse;
import com.company.scopery.modules.specpack.agentsession.application.service.AgentSessionQueryService;
import com.company.scopery.modules.specpack.agentsession.http.request.AdvanceStageRequest;
import com.company.scopery.modules.specpack.agentsession.http.request.StartAgentSessionRequest;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Spec Pack - Agent Sessions")
@RestController
@RequestMapping(SpecPackApiPaths.AGENT_SESSIONS)
public class SpecPackAgentSessionController {

    private final StartAgentSessionAction startAction;
    private final AdvanceStageAction advanceStageAction;
    private final CancelSessionAction cancelAction;
    private final UpdateReadinessAction updateReadinessAction;
    private final AgentSessionQueryService queryService;

    public SpecPackAgentSessionController(StartAgentSessionAction startAction,
                                           AdvanceStageAction advanceStageAction,
                                           CancelSessionAction cancelAction,
                                           UpdateReadinessAction updateReadinessAction,
                                           AgentSessionQueryService queryService) {
        this.startAction = startAction;
        this.advanceStageAction = advanceStageAction;
        this.cancelAction = cancelAction;
        this.updateReadinessAction = updateReadinessAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Start a new Agent Session for a Spec Pack")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AgentSessionResponse> start(@PathVariable UUID projectId,
                                                    @Valid @RequestBody StartAgentSessionRequest request) {
        return ApiResponse.success(startAction.execute(new StartAgentSessionCommand(
                projectId, request.specPackId(), request.scopePackageId()
        )));
    }

    @Operation(summary = "List Agent Sessions for a project")
    @GetMapping
    public ApiResponse<List<AgentSessionResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listByProject(projectId));
    }

    @Operation(summary = "Get Agent Session by ID")
    @GetMapping("/{sessionId}")
    public ApiResponse<AgentSessionResponse> getById(@PathVariable UUID projectId,
                                                      @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.getById(projectId, sessionId));
    }

    @Operation(summary = "Get all stages for an Agent Session")
    @GetMapping("/{sessionId}/stages")
    public ApiResponse<List<AgentStageResponse>> getStages(@PathVariable UUID projectId,
                                                            @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.getStages(sessionId));
    }

    @Operation(summary = "Advance (complete) a stage in an Agent Session")
    @PostMapping("/{sessionId}/stages/advance")
    public ApiResponse<AgentSessionResponse> advanceStage(@PathVariable UUID projectId,
                                                           @PathVariable UUID sessionId,
                                                           @Valid @RequestBody AdvanceStageRequest request) {
        return ApiResponse.success(advanceStageAction.execute(new AdvanceStageCommand(
                projectId, sessionId, request.stageCode(), request.result()
        )));
    }

    @Operation(summary = "Cancel an Agent Session")
    @PostMapping("/{sessionId}/cancel")
    public ApiResponse<AgentSessionResponse> cancel(@PathVariable UUID projectId,
                                                     @PathVariable UUID sessionId) {
        return ApiResponse.success(cancelAction.execute(new CancelSessionCommand(projectId, sessionId)));
    }

    @Operation(summary = "Recalculate and update session readiness")
    @PutMapping("/{sessionId}/readiness")
    public ApiResponse<AgentSessionResponse> updateReadiness(@PathVariable UUID projectId,
                                                              @PathVariable UUID sessionId) {
        return ApiResponse.success(updateReadinessAction.execute(new UpdateReadinessCommand(projectId, sessionId)));
    }
}
