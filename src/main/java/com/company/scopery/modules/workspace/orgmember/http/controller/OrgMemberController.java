package com.company.scopery.modules.workspace.orgmember.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.orgmember.application.action.AddOrgMemberAction;
import com.company.scopery.modules.workspace.orgmember.application.action.ActivateOrgMemberAction;
import com.company.scopery.modules.workspace.orgmember.application.action.RemoveOrgMemberAction;
import com.company.scopery.modules.workspace.orgmember.application.action.SuspendOrgMemberAction;
import com.company.scopery.modules.workspace.orgmember.application.command.AddOrgMemberCommand;
import com.company.scopery.modules.workspace.orgmember.application.command.ChangeOrgMemberStatusCommand;
import com.company.scopery.modules.workspace.orgmember.application.query.SearchOrgMemberQuery;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberAccessResponse;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberResponse;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMemberAccessQueryService;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMemberQueryService;
import com.company.scopery.modules.workspace.orgmember.http.request.AddOrgMemberRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Organization Members")
@RestController
@RequestMapping(WorkspaceApiPaths.ORG_MEMBERS)
public class OrgMemberController {

    private final AddOrgMemberAction addAction;
    private final RemoveOrgMemberAction removeAction;
    private final ActivateOrgMemberAction activateAction;
    private final SuspendOrgMemberAction suspendAction;
    private final OrgMemberQueryService queryService;
    private final OrgMemberAccessQueryService accessQueryService;

    public OrgMemberController(AddOrgMemberAction addAction,
                                RemoveOrgMemberAction removeAction,
                                ActivateOrgMemberAction activateAction,
                                SuspendOrgMemberAction suspendAction,
                                OrgMemberQueryService queryService,
                                OrgMemberAccessQueryService accessQueryService) {
        this.addAction = addAction;
        this.removeAction = removeAction;
        this.activateAction = activateAction;
        this.suspendAction = suspendAction;
        this.queryService = queryService;
        this.accessQueryService = accessQueryService;
    }

    @Operation(summary = "Add a member to an organization")
    @PostMapping
    public ResponseEntity<ApiResponse<OrgMemberResponse>> addMember(
            @PathVariable UUID organizationId,
            @Valid @RequestBody AddOrgMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(addAction.execute(
                new AddOrgMemberCommand(organizationId, request.userId(),
                        request.membershipType() != null ? request.membershipType() : "MEMBER"))));
    }

    @Operation(summary = "Get an organization member by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrgMemberResponse>> getMember(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getMember(id)));
    }

    @Operation(summary = "Hierarchy access map for a person in this organization (workspaces + projects)")
    @GetMapping("/by-user/{userId}/access")
    public ResponseEntity<ApiResponse<OrgMemberAccessResponse>> memberAccess(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(accessQueryService.getAccess(organizationId, userId)));
    }

    @Operation(summary = "List members of an organization")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrgMemberResponse>>> listMembers(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrgMemberResponse> result = queryService.searchMembers(new SearchOrgMemberQuery(
                organizationId, userId, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Remove a member from an organization")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrgMemberResponse>> removeMember(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(removeAction.execute(id)));
    }

    @Operation(summary = "Activate an organization member")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<OrgMemberResponse>> activateMember(
            @PathVariable UUID organizationId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                activateAction.execute(new ChangeOrgMemberStatusCommand(organizationId, id))));
    }

    @Operation(summary = "Suspend an organization member")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<OrgMemberResponse>> suspendMember(
            @PathVariable UUID organizationId, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                suspendAction.execute(new ChangeOrgMemberStatusCommand(organizationId, id))));
    }
}
