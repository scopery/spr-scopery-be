package com.company.scopery.modules.workspace.invitation.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.invitation.api.request.CreateWorkspaceInvitationRequest;
import com.company.scopery.modules.workspace.invitation.application.WorkspaceInvitationApplicationService;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
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

    private final WorkspaceInvitationApplicationService invitationService;

    public WorkspaceInvitationController(WorkspaceInvitationApplicationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create workspace invitation")
    public ApiResponse<WorkspaceInvitationResponse> createInvitation(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateWorkspaceInvitationRequest request) {
        var command = new CreateWorkspaceInvitationCommand(
                workspaceId, request.invitedEmail(), request.maxUses(), request.expiresAt(), request.sendEmail());
        return ApiResponse.success(invitationService.createInvitation(command));
    }

    @PostMapping(WorkspaceApiPaths.WORKSPACE_INVITATION_ACCEPT)
    @Operation(summary = "Accept workspace invitation by code")
    public ApiResponse<WorkspaceInvitationResponse> acceptInvitation(@PathVariable String code) {
        return ApiResponse.success(invitationService.acceptInvitation(code));
    }

    @PatchMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS + "/{id}/revoke")
    @Operation(summary = "Revoke workspace invitation")
    public ApiResponse<WorkspaceInvitationResponse> revokeInvitation(
            @PathVariable UUID workspaceId,
            @PathVariable UUID id) {
        return ApiResponse.success(invitationService.revokeInvitation(id, workspaceId));
    }

    @GetMapping(WorkspaceApiPaths.WORKSPACE_INVITATIONS)
    @Operation(summary = "List workspace invitations")
    public ApiResponse<List<WorkspaceInvitationResponse>> listInvitations(@PathVariable UUID workspaceId) {
        return ApiResponse.success(invitationService.listInvitations(workspaceId));
    }
}
