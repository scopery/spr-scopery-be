package com.company.scopery.modules.workspace.context.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.context.application.action.SwitchWorkspaceAction;
import com.company.scopery.modules.workspace.context.application.command.SwitchWorkspaceCommand;
import com.company.scopery.modules.workspace.context.application.response.WorkspaceContextResponse;
import com.company.scopery.modules.workspace.context.application.service.WorkspaceContextQueryService;
import com.company.scopery.modules.workspace.context.http.request.SwitchWorkspaceRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACE_CONTEXT)
@Tag(name = "Workspace - Context")
public class WorkspaceContextController {

    private final SwitchWorkspaceAction switchAction;
    private final WorkspaceContextQueryService queryService;

    public WorkspaceContextController(SwitchWorkspaceAction switchAction,
                                       WorkspaceContextQueryService queryService) {
        this.switchAction = switchAction;
        this.queryService = queryService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get current workspace context")
    public ApiResponse<WorkspaceContextResponse> getCurrent() {
        return ApiResponse.success(queryService.getCurrentContext());
    }

    @GetMapping("/available")
    @Operation(summary = "Get available workspaces for current user")
    public ApiResponse<List<WorkspaceResponse>> getAvailable() {
        return ApiResponse.success(queryService.getAvailableWorkspaces());
    }

    @PutMapping("/current")
    @Operation(summary = "Switch current workspace")
    public ApiResponse<WorkspaceContextResponse> switchWorkspace(
            @Valid @RequestBody SwitchWorkspaceRequest request) {
        return ApiResponse.success(switchAction.execute(new SwitchWorkspaceCommand(request.workspaceId())));
    }
}
