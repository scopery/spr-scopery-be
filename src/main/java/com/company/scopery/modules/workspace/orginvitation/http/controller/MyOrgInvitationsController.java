package com.company.scopery.modules.workspace.orginvitation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.orginvitation.application.action.AcceptOrgInvitationByIdAction;
import com.company.scopery.modules.workspace.orginvitation.application.response.MyOrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.application.service.MyOrgInvitationsQueryService;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Workspace - My Organization Invitations")
@RestController
public class MyOrgInvitationsController {

    private final MyOrgInvitationsQueryService queryService;
    private final AcceptOrgInvitationByIdAction acceptByIdAction;

    public MyOrgInvitationsController(MyOrgInvitationsQueryService queryService,
                                       AcceptOrgInvitationByIdAction acceptByIdAction) {
        this.queryService = queryService;
        this.acceptByIdAction = acceptByIdAction;
    }

    @Operation(summary = "List pending organization invitations for the current user")
    @GetMapping(WorkspaceApiPaths.ME_ORG_INVITATIONS)
    public ResponseEntity<ApiResponse<List<MyOrgInvitationResponse>>> listMine() {
        return ResponseEntity.ok(ApiResponse.success(queryService.listPendingForCurrentUser()));
    }

    @Operation(summary = "Accept a pending organization invitation by id (email must match)")
    @PostMapping(WorkspaceApiPaths.ME_ORG_INVITATION_ACCEPT)
    public ResponseEntity<ApiResponse<OrgInvitationResponse>> acceptById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(acceptByIdAction.execute(id)));
    }
}
