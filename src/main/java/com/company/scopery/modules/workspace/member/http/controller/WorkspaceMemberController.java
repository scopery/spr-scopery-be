package com.company.scopery.modules.workspace.member.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.member.application.action.ActivateWorkspaceMemberAction;
import com.company.scopery.modules.workspace.member.application.action.AddWorkspaceMemberAction;
import com.company.scopery.modules.workspace.member.application.action.DeactivateWorkspaceMemberAction;
import com.company.scopery.modules.workspace.member.application.command.ActivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.command.AddWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.command.DeactivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.query.SearchWorkspaceMemberQuery;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.application.service.WorkspaceMemberQueryService;
import com.company.scopery.modules.workspace.member.http.request.AddWorkspaceMemberRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Members", description = "Manage workspace members")
@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACE_MEMBERS)
public class WorkspaceMemberController {

    private final AddWorkspaceMemberAction addWorkspaceMemberAction;
    private final ActivateWorkspaceMemberAction activateWorkspaceMemberAction;
    private final DeactivateWorkspaceMemberAction deactivateWorkspaceMemberAction;
    private final WorkspaceMemberQueryService queryService;

    public WorkspaceMemberController(AddWorkspaceMemberAction addWorkspaceMemberAction,
                                      ActivateWorkspaceMemberAction activateWorkspaceMemberAction,
                                      DeactivateWorkspaceMemberAction deactivateWorkspaceMemberAction,
                                      WorkspaceMemberQueryService queryService) {
        this.addWorkspaceMemberAction = addWorkspaceMemberAction;
        this.activateWorkspaceMemberAction = activateWorkspaceMemberAction;
        this.deactivateWorkspaceMemberAction = deactivateWorkspaceMemberAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Add a member to a workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> addMember(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody AddWorkspaceMemberRequest request) {
        AddWorkspaceMemberCommand command = new AddWorkspaceMemberCommand(workspaceId, request.userId());
        return ResponseEntity.ok(ApiResponse.success(addWorkspaceMemberAction.execute(command)));
    }

    @Operation(summary = "Search and list workspace members")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WorkspaceMemberResponse>>> searchMembers(
            @PathVariable UUID workspaceId,
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) UUID userId,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchWorkspaceMemberQuery query = new SearchWorkspaceMemberQuery(workspaceId, userId, status, page, size);
        PageResult<WorkspaceMemberResponse> result = queryService.searchMembers(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a workspace member")
    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> activateMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) {
        ActivateWorkspaceMemberCommand command = new ActivateWorkspaceMemberCommand(workspaceId, memberId);
        return ResponseEntity.ok(ApiResponse.success(activateWorkspaceMemberAction.execute(command)));
    }

    @Operation(summary = "Deactivate a workspace member")
    @PatchMapping("/{memberId}/deactivate")
    public ResponseEntity<ApiResponse<WorkspaceMemberResponse>> deactivateMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID memberId) {
        DeactivateWorkspaceMemberCommand command = new DeactivateWorkspaceMemberCommand(workspaceId, memberId);
        return ResponseEntity.ok(ApiResponse.success(deactivateWorkspaceMemberAction.execute(command)));
    }
}
