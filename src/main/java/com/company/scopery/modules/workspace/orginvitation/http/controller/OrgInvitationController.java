package com.company.scopery.modules.workspace.orginvitation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.orginvitation.application.action.AcceptOrgInvitationAction;
import com.company.scopery.modules.workspace.orginvitation.application.action.CancelOrgInvitationAction;
import com.company.scopery.modules.workspace.orginvitation.application.action.CreateOrgInvitationAction;
import com.company.scopery.modules.workspace.orginvitation.application.command.AcceptOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.command.CreateOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.application.service.OrgInvitationQueryService;
import com.company.scopery.modules.workspace.orginvitation.http.request.CreateOrgInvitationRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Organization Invitations")
@RestController
public class OrgInvitationController {

    private final CreateOrgInvitationAction createAction;
    private final AcceptOrgInvitationAction acceptAction;
    private final CancelOrgInvitationAction cancelAction;
    private final OrgInvitationQueryService queryService;

    public OrgInvitationController(CreateOrgInvitationAction createAction,
                                    AcceptOrgInvitationAction acceptAction,
                                    CancelOrgInvitationAction cancelAction,
                                    OrgInvitationQueryService queryService) {
        this.createAction = createAction;
        this.acceptAction = acceptAction;
        this.cancelAction = cancelAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create an organization invitation")
    @PostMapping(WorkspaceApiPaths.ORG_INVITATIONS)
    public ResponseEntity<ApiResponse<OrgInvitationResponse>> createInvitation(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateOrgInvitationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createAction.execute(
                new CreateOrgInvitationCommand(organizationId, request.inviteeEmail(),
                        request.membershipType(), request.expiresAt()))));
    }

    @Operation(summary = "Get an organization invitation by ID")
    @GetMapping(WorkspaceApiPaths.ORG_INVITATIONS + "/{id}")
    public ResponseEntity<ApiResponse<OrgInvitationResponse>> getInvitation(
            @PathVariable UUID organizationId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getInvitation(id)));
    }

    @Operation(summary = "Accept an organization invitation by token")
    @PostMapping(WorkspaceApiPaths.ORG_INVITATION_ACCEPT)
    public ResponseEntity<ApiResponse<OrgInvitationResponse>> acceptInvitation(
            @PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success(acceptAction.execute(
                new AcceptOrgInvitationCommand(token))));
    }

    @Operation(summary = "Cancel an organization invitation")
    @DeleteMapping(WorkspaceApiPaths.ORG_INVITATIONS + "/{id}")
    public ResponseEntity<ApiResponse<OrgInvitationResponse>> cancelInvitation(
            @PathVariable UUID organizationId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(cancelAction.execute(organizationId, id)));
    }
}
