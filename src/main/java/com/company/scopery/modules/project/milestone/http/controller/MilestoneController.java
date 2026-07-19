package com.company.scopery.modules.project.milestone.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.milestone.application.action.*;
import com.company.scopery.modules.project.milestone.application.command.*;
import com.company.scopery.modules.project.milestone.application.response.MilestoneResponse;
import com.company.scopery.modules.project.milestone.application.service.ProjectMilestoneQueryService;
import com.company.scopery.modules.project.milestone.http.request.CreateProjectMilestoneRequest;
import com.company.scopery.modules.project.milestone.http.request.UpdateProjectMilestoneRequest;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.MILESTONES)
@Tag(name = "Project - Milestones")
public class MilestoneController {

    private final ProjectMilestoneQueryService queryService;
    private final CreateProjectMilestoneAction createAction;
    private final UpdateProjectMilestoneAction updateAction;
    private final AchieveProjectMilestoneAction achieveAction;
    private final ArchiveProjectMilestoneAction archiveAction;

    public MilestoneController(ProjectMilestoneQueryService queryService,
                               CreateProjectMilestoneAction createAction,
                               UpdateProjectMilestoneAction updateAction,
                               AchieveProjectMilestoneAction achieveAction,
                               ArchiveProjectMilestoneAction archiveAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.achieveAction = achieveAction;
        this.archiveAction = archiveAction;
    }

    @GetMapping
    @Operation(summary = "List project milestones")
    public ApiResponse<List<MilestoneResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.list(projectId));
    }

    @GetMapping("/{milestoneId}")
    @Operation(summary = "Get project milestone")
    public ApiResponse<MilestoneResponse> get(@PathVariable UUID projectId, @PathVariable UUID milestoneId) {
        return ApiResponse.success(queryService.get(projectId, milestoneId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create project milestone")
    public ApiResponse<MilestoneResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectMilestoneRequest request) {
        return ApiResponse.success(createAction.execute(new CreateProjectMilestoneCommand(
                projectId, request.projectPhaseId(), request.wbsNodeId(),
                request.code(), request.name(), request.description(),
                request.milestoneDate(), request.sortOrder())));
    }

    @PutMapping("/{milestoneId}")
    @Operation(summary = "Update project milestone")
    public ApiResponse<MilestoneResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID milestoneId,
            @Valid @RequestBody UpdateProjectMilestoneRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectMilestoneCommand(
                projectId, milestoneId, request.projectPhaseId(), request.wbsNodeId(),
                request.code(), request.name(), request.description(),
                request.milestoneDate(), request.sortOrder())));
    }

    @PatchMapping("/{milestoneId}/achieve")
    @Operation(summary = "Achieve project milestone")
    public ApiResponse<MilestoneResponse> achieve(
            @PathVariable UUID projectId,
            @PathVariable UUID milestoneId) {
        return ApiResponse.success(achieveAction.execute(
                new AchieveProjectMilestoneCommand(projectId, milestoneId)));
    }

    @PatchMapping("/{milestoneId}/archive")
    @Operation(summary = "Archive project milestone")
    public ApiResponse<MilestoneResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID milestoneId) {
        return ApiResponse.success(archiveAction.execute(
                new ArchiveProjectMilestoneCommand(projectId, milestoneId)));
    }
}
