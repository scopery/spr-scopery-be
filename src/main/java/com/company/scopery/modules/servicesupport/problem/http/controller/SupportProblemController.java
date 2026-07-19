package com.company.scopery.modules.servicesupport.problem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.problem.application.action.*;
import com.company.scopery.modules.servicesupport.problem.application.command.*;
import com.company.scopery.modules.servicesupport.problem.application.response.SupportProblemResponse;
import com.company.scopery.modules.servicesupport.problem.application.service.SupportProblemQueryService;
import com.company.scopery.modules.servicesupport.problem.http.request.*;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Problems")
public class SupportProblemController {
    private final SupportProblemQueryService query;
    private final CreateProblemAction createAction;
    private final ResolveProblemAction resolveAction;
    private final CloseProblemAction closeAction;

    public SupportProblemController(SupportProblemQueryService query, CreateProblemAction createAction,
            ResolveProblemAction resolveAction, CloseProblemAction closeAction) {
        this.query = query; this.createAction = createAction;
        this.resolveAction = resolveAction; this.closeAction = closeAction;
    }

    @GetMapping(SupportApiPaths.PROBLEMS)
    @Operation(summary = "List problems in workspace")
    public ApiResponse<List<SupportProblemResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.PROBLEMS)
    @Operation(summary = "Create problem record")
    public ApiResponse<SupportProblemResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateProblemRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateProblemCommand(req.title(), req.projectId())));
    }

    @PostMapping(SupportApiPaths.PROBLEM_RESOLVE)
    @Operation(summary = "Resolve problem")
    public ApiResponse<SupportProblemResponse> resolve(@PathVariable UUID workspaceId,
            @PathVariable UUID problemId, @RequestBody @Valid ResolveProblemRequest req) {
        return ApiResponse.success(resolveAction.execute(workspaceId, problemId,
                new ResolveProblemCommand(req.rootCause(), req.workaround(), req.resolvedBy())));
    }

    @PostMapping(SupportApiPaths.PROBLEM_CLOSE)
    @Operation(summary = "Close problem")
    public ApiResponse<SupportProblemResponse> close(@PathVariable UUID workspaceId,
            @PathVariable UUID problemId, @RequestParam(required = false) UUID closedBy) {
        return ApiResponse.success(closeAction.execute(workspaceId, problemId, closedBy));
    }
}
