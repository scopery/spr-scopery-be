package com.company.scopery.modules.workspace.invitation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.invitation.application.action.AcceptInvitationAction;
import com.company.scopery.modules.workspace.invitation.application.action.CreateInvitationAction;
import com.company.scopery.modules.workspace.invitation.application.action.RevokeInvitationAction;
import com.company.scopery.modules.workspace.invitation.application.command.AcceptInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.command.RevokeInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.application.service.InvitationQueryService;
import com.company.scopery.modules.workspace.invitation.http.request.CreateWorkspaceInvitationRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Workspace - Invitations")
public class WorkspaceInvitationController {

    private final CreateInvitationAction createInvitationAction;
    private final AcceptInvitationAction acceptInvitationAction;
    private final RevokeInvitationAction revokeInvitationAction;
    private final InvitationQueryService queryService;

    public WorkspaceInvitationController(CreateInvitationAction createInvitationAction,
                                          AcceptInvitationAction acceptInvitationAction,
                                          RevokeInvitationAction revokeInvitationAction,
                                          InvitationQueryService queryService) {
        this.createInvitationAction = createInvitationAction;
        this.acceptInvitationAction = acceptInvitationAction;
        this.revokeInvitationAction = revokeInvitationAction;
        this.queryService = queryService;
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create workspace invitation")
    public ApiResponse<WorkspaceInvitationResponse> createInvitation(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateWorkspaceInvitationRequest request) {
        var command = new CreateWorkspaceInvitationCommand(
                workspaceId, request.invitedEmail(), request.maxUses(), request.expiresAt(), request.sendEmail());
        return ApiResponse.success(createInvitationAction.execute(command));
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_INVITATION_ACCEPT)
    @Operation(summary = "Accept workspace invitation by code")
    public ApiResponse<WorkspaceInvitationResponse> acceptInvitation(@PathVariable String code) {
        return ApiResponse.success(acceptInvitationAction.execute(new AcceptInvitationCommand(code)));
    }

    @PatchMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS + "/{id}/revoke")
    @Operation(summary = "Revoke workspace invitation")
    public ApiResponse<WorkspaceInvitationResponse> revokeInvitation(
            @PathVariable UUID workspaceId,
            @PathVariable UUID id) {
        return ApiResponse.success(revokeInvitationAction.execute(new RevokeInvitationCommand(id, workspaceId)));
    }

    @GetMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS)
    @Operation(summary = "List workspace invitations")
    public ApiResponse<List<WorkspaceInvitationResponse>> listInvitations(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listInvitations(workspaceId));
    }
}
