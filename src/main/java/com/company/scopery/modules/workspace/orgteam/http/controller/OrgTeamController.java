package com.company.scopery.modules.workspace.orgteam.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.orgteam.application.action.*;
import com.company.scopery.modules.workspace.orgteam.application.command.*;
import com.company.scopery.modules.workspace.orgteam.application.query.SearchOrgTeamQuery;
import com.company.scopery.modules.workspace.orgteam.application.response.*;
import com.company.scopery.modules.workspace.orgteam.application.service.OrgTeamQueryService;
import com.company.scopery.modules.workspace.orgteam.http.request.*;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Workspace - Org Teams")
public class OrgTeamController {

    private final OrgTeamQueryService queryService;
    private final CreateOrgTeamAction createOrgTeamAction;
    private final UpdateOrgTeamAction updateOrgTeamAction;
    private final ActivateOrgTeamAction activateOrgTeamAction;
    private final ArchiveOrgTeamAction archiveOrgTeamAction;
    private final AddOrgTeamMemberAction addOrgTeamMemberAction;
    private final RemoveOrgTeamMemberAction removeOrgTeamMemberAction;
    private final AssignOrgTeamToWorkspaceAction assignOrgTeamToWorkspaceAction;
    private final RevokeOrgTeamWorkspaceAssignmentAction revokeOrgTeamWorkspaceAssignmentAction;

    public OrgTeamController(OrgTeamQueryService queryService,
                              CreateOrgTeamAction createOrgTeamAction,
                              UpdateOrgTeamAction updateOrgTeamAction,
                              ActivateOrgTeamAction activateOrgTeamAction,
                              ArchiveOrgTeamAction archiveOrgTeamAction,
                              AddOrgTeamMemberAction addOrgTeamMemberAction,
                              RemoveOrgTeamMemberAction removeOrgTeamMemberAction,
                              AssignOrgTeamToWorkspaceAction assignOrgTeamToWorkspaceAction,
                              RevokeOrgTeamWorkspaceAssignmentAction revokeOrgTeamWorkspaceAssignmentAction) {
        this.queryService = queryService;
        this.createOrgTeamAction = createOrgTeamAction;
        this.updateOrgTeamAction = updateOrgTeamAction;
        this.activateOrgTeamAction = activateOrgTeamAction;
        this.archiveOrgTeamAction = archiveOrgTeamAction;
        this.addOrgTeamMemberAction = addOrgTeamMemberAction;
        this.removeOrgTeamMemberAction = removeOrgTeamMemberAction;
        this.assignOrgTeamToWorkspaceAction = assignOrgTeamToWorkspaceAction;
        this.revokeOrgTeamWorkspaceAssignmentAction = revokeOrgTeamWorkspaceAssignmentAction;
    }

    @PostMapping(WorkspaceApiPaths.ORG_TEAMS)
    @Operation(summary = "Create an org team")
    public ApiResponse<OrgTeamResponse> create(@PathVariable UUID organizationId,
                                                @Valid @RequestBody CreateOrgTeamRequest request) {
        return ApiResponse.success(createOrgTeamAction.execute(
                new CreateOrgTeamCommand(organizationId, request.name(), request.code(), request.description())));
    }

    @GetMapping(WorkspaceApiPaths.ORG_TEAMS + "/{teamId}")
    @Operation(summary = "Get an org team by ID")
    public ApiResponse<OrgTeamResponse> get(@PathVariable UUID organizationId,
                                             @PathVariable UUID teamId) {
        return ApiResponse.success(queryService.getTeam(teamId));
    }

    @GetMapping(WorkspaceApiPaths.ORG_TEAMS)
    @Operation(summary = "Search org teams in an organization")
    public ApiResponse<PageResponse<OrgTeamResponse>> search(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrgTeamResponse> result = queryService.searchTeams(
                new SearchOrgTeamQuery(organizationId, keyword, status, page, size));
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping(WorkspaceApiPaths.ORG_TEAMS + "/{teamId}")
    @Operation(summary = "Update an org team")
    public ApiResponse<OrgTeamResponse> update(@PathVariable UUID organizationId,
                                                @PathVariable UUID teamId,
                                                @Valid @RequestBody UpdateOrgTeamRequest request) {
        return ApiResponse.success(updateOrgTeamAction.execute(
                new UpdateOrgTeamCommand(teamId, organizationId, request.name(), request.description())));
    }

    @PatchMapping(WorkspaceApiPaths.ORG_TEAMS + "/{teamId}/activate")
    @Operation(summary = "Activate an org team")
    public ApiResponse<OrgTeamResponse> activate(@PathVariable UUID organizationId,
                                                  @PathVariable UUID teamId) {
        return ApiResponse.success(activateOrgTeamAction.execute(new ActivateOrgTeamCommand(teamId, organizationId)));
    }

    @PatchMapping(WorkspaceApiPaths.ORG_TEAMS + "/{teamId}/archive")
    @Operation(summary = "Archive an org team")
    public ApiResponse<OrgTeamResponse> archive(@PathVariable UUID organizationId,
                                                 @PathVariable UUID teamId) {
        return ApiResponse.success(archiveOrgTeamAction.execute(new ArchiveOrgTeamCommand(teamId, organizationId)));
    }

    @PostMapping(WorkspaceApiPaths.ORG_TEAM_MEMBERS)
    @Operation(summary = "Add a member to an org team")
    public ApiResponse<OrgTeamMemberResponse> addMember(@PathVariable UUID organizationId,
                                                         @PathVariable UUID teamId,
                                                         @Valid @RequestBody AddOrgTeamMemberRequest request) {
        return ApiResponse.success(addOrgTeamMemberAction.execute(
                new AddOrgTeamMemberCommand(teamId, organizationId, request.userId())));
    }

    @GetMapping(WorkspaceApiPaths.ORG_TEAM_MEMBERS)
    @Operation(summary = "List members of an org team")
    public ApiResponse<PageResponse<OrgTeamMemberResponse>> listMembers(
            @PathVariable UUID organizationId,
            @PathVariable UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrgTeamMemberResponse> result = queryService.listMembers(teamId, page, size);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @DeleteMapping(WorkspaceApiPaths.ORG_TEAM_MEMBERS + "/{userId}")
    @Operation(summary = "Remove a member from an org team")
    public ApiResponse<Void> removeMember(@PathVariable UUID organizationId,
                                           @PathVariable UUID teamId,
                                           @PathVariable UUID userId) {
        removeOrgTeamMemberAction.execute(new RemoveOrgTeamMemberCommand(teamId, organizationId, userId));
        return ApiResponse.success(null);
    }

    @PostMapping(WorkspaceApiPaths.ORG_TEAM_WORKSPACE_ASSIGNMENTS)
    @Operation(summary = "Assign an org team to a workspace")
    public ApiResponse<OrgTeamWorkspaceAssignmentResponse> assignToWorkspace(
            @PathVariable UUID organizationId,
            @PathVariable UUID teamId,
            @Valid @RequestBody AssignOrgTeamToWorkspaceRequest request) {
        return ApiResponse.success(assignOrgTeamToWorkspaceAction.execute(
                new AssignOrgTeamToWorkspaceCommand(organizationId, teamId, request.workspaceId())));
    }

    @GetMapping(WorkspaceApiPaths.ORG_TEAM_WORKSPACE_ASSIGNMENTS)
    @Operation(summary = "List workspace assignments for an org team")
    public ApiResponse<PageResponse<OrgTeamWorkspaceAssignmentResponse>> listWorkspaceAssignments(
            @PathVariable UUID organizationId,
            @PathVariable UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrgTeamWorkspaceAssignmentResponse> result = queryService.listWorkspaceAssignments(teamId, page, size);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @DeleteMapping(WorkspaceApiPaths.ORG_TEAM_WORKSPACE_ASSIGNMENTS + "/{assignmentId}")
    @Operation(summary = "Revoke a workspace assignment from an org team")
    public ApiResponse<OrgTeamWorkspaceAssignmentResponse> revokeWorkspaceAssignment(
            @PathVariable UUID organizationId,
            @PathVariable UUID teamId,
            @PathVariable UUID assignmentId) {
        return ApiResponse.success(revokeOrgTeamWorkspaceAssignmentAction.execute(
                new RevokeOrgTeamWorkspaceAssignmentCommand(organizationId, assignmentId)));
    }
}
