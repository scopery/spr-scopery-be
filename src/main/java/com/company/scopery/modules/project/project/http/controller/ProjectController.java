package com.company.scopery.modules.project.project.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.project.application.action.ActivateProjectAction;
import com.company.scopery.modules.project.project.application.action.ArchiveProjectAction;
import com.company.scopery.modules.project.project.application.action.CompleteProjectAction;
import com.company.scopery.modules.project.project.application.action.CreateProjectAction;
import com.company.scopery.modules.project.project.application.action.HoldProjectAction;
import com.company.scopery.modules.project.project.application.action.UpdateProjectAction;
import com.company.scopery.modules.project.project.application.command.ActivateProjectCommand;
import com.company.scopery.modules.project.project.application.command.ArchiveProjectCommand;
import com.company.scopery.modules.project.project.application.command.CompleteProjectCommand;
import com.company.scopery.modules.project.project.application.command.CreateProjectCommand;
import com.company.scopery.modules.project.project.application.command.HoldProjectCommand;
import com.company.scopery.modules.project.project.application.command.UpdateProjectCommand;
import com.company.scopery.modules.project.project.application.query.SearchProjectQuery;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.application.service.ProjectQueryService;
import com.company.scopery.modules.project.project.http.request.CreateProjectRequest;
import com.company.scopery.modules.project.project.http.request.UpdateProjectRequest;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Project - Projects", description = "Manage projects")
@RestController
@RequestMapping(ProjectApiPaths.PROJECTS)
public class ProjectController {

    private final ProjectQueryService queryService;
    private final CreateProjectAction createProjectAction;
    private final UpdateProjectAction updateProjectAction;
    private final ActivateProjectAction activateProjectAction;
    private final HoldProjectAction holdProjectAction;
    private final CompleteProjectAction completeProjectAction;
    private final ArchiveProjectAction archiveProjectAction;

    public ProjectController(ProjectQueryService queryService,
                              CreateProjectAction createProjectAction,
                              UpdateProjectAction updateProjectAction,
                              ActivateProjectAction activateProjectAction,
                              HoldProjectAction holdProjectAction,
                              CompleteProjectAction completeProjectAction,
                              ArchiveProjectAction archiveProjectAction) {
        this.queryService = queryService;
        this.createProjectAction = createProjectAction;
        this.updateProjectAction = updateProjectAction;
        this.activateProjectAction = activateProjectAction;
        this.holdProjectAction = holdProjectAction;
        this.completeProjectAction = completeProjectAction;
        this.archiveProjectAction = archiveProjectAction;
    }

    @Operation(summary = "Create a new project")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        CreateProjectCommand command = new CreateProjectCommand(
                request.workspaceId(),
                request.code(),
                request.name(),
                request.description(),
                request.ownerUserId(),
                request.defaultCurrency(),
                request.plannedStartDate(),
                request.plannedEndDate()
        );
        return ResponseEntity.ok(ApiResponse.success(createProjectAction.execute(command)));
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getProject(id)));
    }

    @Operation(summary = "Search and list projects with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> searchProjects(
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchProjectQuery query = new SearchProjectQuery(workspaceId, keyword, status, page, size);
        PageResult<ProjectResponse> result = queryService.searchProjects(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Update a project")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {
        UpdateProjectCommand command = new UpdateProjectCommand(
                id,
                request.name(),
                request.description(),
                request.ownerUserId(),
                request.defaultCurrency(),
                request.plannedStartDate(),
                request.plannedEndDate()
        );
        return ResponseEntity.ok(ApiResponse.success(updateProjectAction.execute(command)));
    }

    @Operation(summary = "Activate a project")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ProjectResponse>> activateProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                activateProjectAction.execute(new ActivateProjectCommand(id))));
    }

    @Operation(summary = "Put a project on hold")
    @PatchMapping("/{id}/hold")
    public ResponseEntity<ApiResponse<ProjectResponse>> holdProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                holdProjectAction.execute(new HoldProjectCommand(id))));
    }

    @Operation(summary = "Complete a project")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ProjectResponse>> completeProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                completeProjectAction.execute(new CompleteProjectCommand(id))));
    }

    @Operation(summary = "Archive a project")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ProjectResponse>> archiveProject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                archiveProjectAction.execute(new ArchiveProjectCommand(id))));
    }
}
