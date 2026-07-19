package com.company.scopery.modules.resourcecapacity.projectallocation.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.action.ActivateProjectResourceAllocationAction;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.action.ArchiveProjectResourceAllocationAction;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.action.CreateProjectResourceAllocationAction;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.action.DeactivateProjectResourceAllocationAction;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.action.UpdateProjectResourceAllocationAction;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.ActivateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.ArchiveProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.CreateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.DeactivateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.UpdateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.query.SearchProjectResourceAllocationQuery;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.response.ProjectResourceAllocationResponse;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.service.ProjectResourceAllocationQueryService;
import com.company.scopery.modules.resourcecapacity.projectallocation.http.request.CreateProjectResourceAllocationRequest;
import com.company.scopery.modules.resourcecapacity.projectallocation.http.request.UpdateProjectResourceAllocationRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping(CapacityApiPaths.PROJECT_ALLOCATIONS)
@Tag(name = "Resource Capacity - Project Allocations")
public class ProjectResourceAllocationController {

    private final ProjectResourceAllocationQueryService queryService;
    private final CreateProjectResourceAllocationAction createAction;
    private final UpdateProjectResourceAllocationAction updateAction;
    private final ActivateProjectResourceAllocationAction activateAction;
    private final DeactivateProjectResourceAllocationAction deactivateAction;
    private final ArchiveProjectResourceAllocationAction archiveAction;

    public ProjectResourceAllocationController(ProjectResourceAllocationQueryService queryService,
                                               CreateProjectResourceAllocationAction createAction,
                                               UpdateProjectResourceAllocationAction updateAction,
                                               ActivateProjectResourceAllocationAction activateAction,
                                               DeactivateProjectResourceAllocationAction deactivateAction,
                                               ArchiveProjectResourceAllocationAction archiveAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
    }

    @PostMapping
    @Operation(summary = "Create a project resource allocation")
    public ApiResponse<ProjectResourceAllocationResponse> create(
            @RequestParam UUID workspaceId,
            @Valid @RequestBody CreateProjectResourceAllocationRequest request) {
        var cmd = new CreateProjectResourceAllocationCommand(
                workspaceId, request.projectId(), request.workspaceMemberId(), request.allocationPercent(),
                request.allocationType(), request.startDate(), request.endDate(), request.notes());
        return ApiResponse.success(createAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project resource allocation by ID")
    public ApiResponse<ProjectResourceAllocationResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(queryService.getAllocation(id));
    }

    @GetMapping
    @Operation(summary = "Search project resource allocations")
    public ApiResponse<PageResponse<ProjectResourceAllocationResponse>> search(
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID workspaceMemberId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchProjectResourceAllocationQuery(
                workspaceId, projectId, workspaceMemberId, userId, status, page, size);
        PageResult<ProjectResourceAllocationResponse> result = queryService.searchAllocations(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project resource allocation")
    public ApiResponse<ProjectResourceAllocationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectResourceAllocationRequest request) {
        var cmd = new UpdateProjectResourceAllocationCommand(
                id, request.allocationPercent(), request.allocationType(),
                request.startDate(), request.endDate(), request.notes());
        return ApiResponse.success(updateAction.execute(cmd));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a project resource allocation")
    public ApiResponse<ProjectResourceAllocationResponse> activate(@PathVariable UUID id) {
        return ApiResponse.success(activateAction.execute(new ActivateProjectResourceAllocationCommand(id)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a project resource allocation")
    public ApiResponse<ProjectResourceAllocationResponse> deactivate(@PathVariable UUID id) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateProjectResourceAllocationCommand(id)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a project resource allocation")
    public ApiResponse<ProjectResourceAllocationResponse> archive(@PathVariable UUID id) {
        return ApiResponse.success(archiveAction.execute(new ArchiveProjectResourceAllocationCommand(id)));
    }
}
