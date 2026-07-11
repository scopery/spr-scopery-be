package com.company.scopery.modules.workspace.team.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.team.application.action.ActivateTeamAction;
import com.company.scopery.modules.workspace.team.application.action.AddTeamMemberAction;
import com.company.scopery.modules.workspace.team.application.action.ArchiveTeamAction;
import com.company.scopery.modules.workspace.team.application.action.CreateTeamAction;
import com.company.scopery.modules.workspace.team.application.action.RemoveTeamMemberAction;
import com.company.scopery.modules.workspace.team.application.action.UpdateTeamAction;
import com.company.scopery.modules.workspace.team.application.command.AddTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.command.RemoveTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamMemberQuery;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamQuery;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.application.service.TeamQueryService;
import com.company.scopery.modules.workspace.team.http.request.AddTeamMemberRequest;
import com.company.scopery.modules.workspace.team.http.request.CreateTeamRequest;
import com.company.scopery.modules.workspace.team.http.request.UpdateTeamRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Teams", description = "Manage workspace teams")
@RestController
@RequestMapping(WorkspaceApiPaths.TEAMS)
public class TeamController {

    private final CreateTeamAction createTeamAction;
    private final UpdateTeamAction updateTeamAction;
    private final ActivateTeamAction activateTeamAction;
    private final ArchiveTeamAction archiveTeamAction;
    private final AddTeamMemberAction addTeamMemberAction;
    private final RemoveTeamMemberAction removeTeamMemberAction;
    private final TeamQueryService queryService;

    public TeamController(CreateTeamAction createTeamAction,
                          UpdateTeamAction updateTeamAction,
                          ActivateTeamAction activateTeamAction,
                          ArchiveTeamAction archiveTeamAction,
                          AddTeamMemberAction addTeamMemberAction,
                          RemoveTeamMemberAction removeTeamMemberAction,
                          TeamQueryService queryService) {
        this.createTeamAction = createTeamAction;
        this.updateTeamAction = updateTeamAction;
        this.activateTeamAction = activateTeamAction;
        this.archiveTeamAction = archiveTeamAction;
        this.addTeamMemberAction = addTeamMemberAction;
        this.removeTeamMemberAction = removeTeamMemberAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new team in a workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateTeamRequest request) {
        CreateTeamCommand command = new CreateTeamCommand(
                workspaceId, request.name(), request.code(), request.description());
        return ResponseEntity.ok(ApiResponse.success(createTeamAction.execute(command)));
    }

    @Operation(summary = "Update an existing team")
    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @Valid @RequestBody UpdateTeamRequest request) {
        UpdateTeamCommand command = new UpdateTeamCommand(workspaceId, teamId, request.name(), request.description());
        return ResponseEntity.ok(ApiResponse.success(updateTeamAction.execute(command)));
    }

    @Operation(summary = "Get team detail by ID")
    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getTeam(workspaceId, teamId)));
    }

    @Operation(summary = "Search and list teams in a workspace")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TeamResponse>>> searchTeams(
            @PathVariable UUID workspaceId,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, ARCHIVED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchTeamQuery query = new SearchTeamQuery(workspaceId, status, page, size);
        PageResult<TeamResponse> result = queryService.searchTeams(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a team")
    @PatchMapping("/{teamId}/activate")
    public ResponseEntity<ApiResponse<TeamResponse>> activateTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(ApiResponse.success(activateTeamAction.execute(workspaceId, teamId)));
    }

    @Operation(summary = "Archive a team")
    @PatchMapping("/{teamId}/archive")
    public ResponseEntity<ApiResponse<TeamResponse>> archiveTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(ApiResponse.success(archiveTeamAction.execute(workspaceId, teamId)));
    }

    @Operation(summary = "Add a member to a team")
    @PostMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addTeamMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @Valid @RequestBody AddTeamMemberRequest request) {
        AddTeamMemberCommand command = new AddTeamMemberCommand(workspaceId, teamId, request.userId());
        return ResponseEntity.ok(ApiResponse.success(addTeamMemberAction.execute(command)));
    }

    @Operation(summary = "Remove a member from a team")
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @PathVariable UUID userId) {
        RemoveTeamMemberCommand command = new RemoveTeamMemberCommand(workspaceId, teamId, userId);
        removeTeamMemberAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "List members of a team")
    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<PageResponse<TeamMemberResponse>>> searchTeamMembers(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchTeamMemberQuery query = new SearchTeamMemberQuery(teamId, page, size);
        PageResult<TeamMemberResponse> result = queryService.searchTeamMembers(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
