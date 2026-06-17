package com.company.scopery.modules.workspace.joinrequest.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.joinrequest.api.request.CreateWorkspaceJoinRequestRequest;
import com.company.scopery.modules.workspace.joinrequest.api.request.ReviewWorkspaceJoinRequestRequest;
import com.company.scopery.modules.workspace.joinrequest.application.WorkspaceJoinRequestApplicationService;
import com.company.scopery.modules.workspace.joinrequest.application.command.CreateWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.command.ReviewWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Workspace - Join Requests")
public class WorkspaceJoinRequestController {

    private final WorkspaceJoinRequestApplicationService joinRequestService;

    public WorkspaceJoinRequestController(WorkspaceJoinRequestApplicationService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create join request via workspace ID in path")
    public ApiResponse<WorkspaceJoinRequestResponse> createByWorkspaceId(
            @PathVariable UUID workspaceId,
            @RequestBody CreateWorkspaceJoinRequestRequest request) {
        var command = new CreateWorkspaceJoinRequestCommand(workspaceId, null, request.message());
        return ApiResponse.success(joinRequestService.createJoinRequest(command));
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS_OPEN)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create join request via workspace ID or code in body")
    public ApiResponse<WorkspaceJoinRequestResponse> createOpen(
            @RequestBody CreateWorkspaceJoinRequestRequest request) {
        var command = new CreateWorkspaceJoinRequestCommand(
                request.workspaceId(), request.workspaceCode(), request.message());
        return ApiResponse.success(joinRequestService.createJoinRequest(command));
    }

    @PatchMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS + "/{id}/approve")
    @Operation(summary = "Approve join request")
    public ApiResponse<WorkspaceJoinRequestResponse> approve(
            @PathVariable UUID workspaceId,
            @PathVariable UUID id) {
        return ApiResponse.success(joinRequestService.approveJoinRequest(
                new ReviewWorkspaceJoinRequestCommand(id, workspaceId, null)));
    }

    @PatchMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS + "/{id}/reject")
    @Operation(summary = "Reject join request")
    public ApiResponse<WorkspaceJoinRequestResponse> reject(
            @PathVariable UUID workspaceId,
            @PathVariable UUID id,
            @RequestBody(required = false) ReviewWorkspaceJoinRequestRequest request) {
        String note = request != null ? request.reviewNote() : null;
        return ApiResponse.success(joinRequestService.rejectJoinRequest(
                new ReviewWorkspaceJoinRequestCommand(id, workspaceId, note)));
    }

    @DeleteMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS + "/{id}")
    @Operation(summary = "Cancel own join request")
    public ApiResponse<WorkspaceJoinRequestResponse> cancel(
            @PathVariable UUID workspaceId,
            @PathVariable UUID id) {
        return ApiResponse.success(joinRequestService.cancelJoinRequest(id));
    }

    @GetMapping(WorkspaceApiPaths.WORKSPACE_JOIN_REQUESTS)
    @Operation(summary = "List join requests for a workspace")
    public ApiResponse<List<WorkspaceJoinRequestResponse>> list(
            @PathVariable UUID workspaceId,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(joinRequestService.listJoinRequests(workspaceId, status));
    }
}
