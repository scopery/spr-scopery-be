package com.company.scopery.modules.workspace.team.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.team.api.request.AddTeamMemberRequest;
import com.company.scopery.modules.workspace.team.api.request.CreateTeamRequest;
import com.company.scopery.modules.workspace.team.api.request.UpdateTeamRequest;
import com.company.scopery.modules.workspace.team.application.TeamApplicationService;
import com.company.scopery.modules.workspace.team.application.TeamMemberApplicationService;
import com.company.scopery.modules.workspace.team.application.command.AddTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.command.RemoveTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamMemberQuery;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamQuery;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
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

    private final TeamApplicationService teamApplicationService;
    private final TeamMemberApplicationService teamMemberApplicationService;

    public TeamController(TeamApplicationService teamApplicationService,
                          TeamMemberApplicationService teamMemberApplicationService) {
        this.teamApplicationService = teamApplicationService;
        this.teamMemberApplicationService = teamMemberApplicationService;
    }

    @Operation(summary = "Create a new team in a workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateTeamRequest request) {
        CreateTeamCommand command = new CreateTeamCommand(
                workspaceId, request.name(), request.code(), request.description());
        TeamResponse response = teamApplicationService.createTeam(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing team")
    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @Valid @RequestBody UpdateTeamRequest request) {
        UpdateTeamCommand command = new UpdateTeamCommand(workspaceId, teamId, request.name(), request.description());
        TeamResponse response = teamApplicationService.updateTeam(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get team detail by ID")
    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        TeamResponse response = teamApplicationService.getTeam(workspaceId, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list teams in a workspace")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TeamResponse>>> searchTeams(
            @PathVariable UUID workspaceId,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, ARCHIVED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchTeamQuery query = new SearchTeamQuery(workspaceId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                teamApplicationService.searchTeams(query))));
    }

    @Operation(summary = "Activate a team")
    @PatchMapping("/{teamId}/activate")
    public ResponseEntity<ApiResponse<TeamResponse>> activateTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        TeamResponse response = teamApplicationService.activateTeam(workspaceId, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Archive a team")
    @PatchMapping("/{teamId}/archive")
    public ResponseEntity<ApiResponse<TeamResponse>> archiveTeam(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId) {
        TeamResponse response = teamApplicationService.archiveTeam(workspaceId, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Add a member to a team")
    @PostMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addTeamMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @Valid @RequestBody AddTeamMemberRequest request) {
        AddTeamMemberCommand command = new AddTeamMemberCommand(workspaceId, teamId, request.userId());
        TeamMemberResponse response = teamMemberApplicationService.addTeamMember(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Remove a member from a team")
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID teamId,
            @PathVariable UUID userId) {
        RemoveTeamMemberCommand command = new RemoveTeamMemberCommand(workspaceId, teamId, userId);
        teamMemberApplicationService.removeTeamMember(command);
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
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                teamMemberApplicationService.searchTeamMembers(query))));
    }
}
