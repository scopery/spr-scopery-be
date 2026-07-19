package com.company.scopery.modules.project.templateversion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.template.application.action.ApplyProjectTemplateAction;
import com.company.scopery.modules.project.template.application.command.ApplyProjectTemplateCommand;
import com.company.scopery.modules.project.template.http.request.ApplyProjectTemplateRequest;
import com.company.scopery.modules.project.templateversion.application.action.ArchiveProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.action.CreateProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.action.DuplicateProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.action.PublishProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.action.UpdateProjectTemplateVersionAction;
import com.company.scopery.modules.project.templateversion.application.command.ArchiveProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.command.CreateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.command.DuplicateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.command.PublishProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.command.UpdateProjectTemplateVersionCommand;
import com.company.scopery.modules.project.templateversion.application.response.ProjectTemplateVersionResponse;
import com.company.scopery.modules.project.templateversion.application.service.ProjectTemplateVersionQueryService;
import com.company.scopery.modules.project.templateversion.http.request.CreateProjectTemplateVersionRequest;
import com.company.scopery.modules.project.templateversion.http.request.UpdateProjectTemplateVersionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_TEMPLATE_VERSIONS)
@Tag(name = "Project - Template Versions")
public class ProjectTemplateVersionController {

    private final CreateProjectTemplateVersionAction createAction;
    private final UpdateProjectTemplateVersionAction updateAction;
    private final PublishProjectTemplateVersionAction publishAction;
    private final ArchiveProjectTemplateVersionAction archiveAction;
    private final DuplicateProjectTemplateVersionAction duplicateAction;
    private final ApplyProjectTemplateAction applyAction;
    private final ProjectTemplateVersionQueryService queryService;

    public ProjectTemplateVersionController(CreateProjectTemplateVersionAction createAction,
                                            UpdateProjectTemplateVersionAction updateAction,
                                            PublishProjectTemplateVersionAction publishAction,
                                            ArchiveProjectTemplateVersionAction archiveAction,
                                            DuplicateProjectTemplateVersionAction duplicateAction,
                                            ApplyProjectTemplateAction applyAction,
                                            ProjectTemplateVersionQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.publishAction = publishAction;
        this.archiveAction = archiveAction;
        this.duplicateAction = duplicateAction;
        this.applyAction = applyAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a template version")
    public ResponseEntity<ApiResponse<ProjectTemplateVersionResponse>> create(
            @PathVariable UUID templateId,
            @Valid @RequestBody CreateProjectTemplateVersionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                createAction.execute(new CreateProjectTemplateVersionCommand(
                        templateId, request.name(), request.description()))));
    }

    @GetMapping
    @Operation(summary = "List template versions")
    public ApiResponse<List<ProjectTemplateVersionResponse>> list(@PathVariable UUID templateId) {
        return ApiResponse.success(queryService.list(templateId));
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Get a template version")
    public ApiResponse<ProjectTemplateVersionResponse> get(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.get(templateId, versionId));
    }

    @PutMapping("/{versionId}")
    @Operation(summary = "Update a draft template version")
    public ApiResponse<ProjectTemplateVersionResponse> update(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody UpdateProjectTemplateVersionRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectTemplateVersionCommand(
                templateId, versionId, request.name(), request.description())));
    }

    @PatchMapping("/{versionId}/publish")
    @Operation(summary = "Publish a template version")
    public ApiResponse<ProjectTemplateVersionResponse> publish(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(publishAction.execute(
                new PublishProjectTemplateVersionCommand(templateId, versionId)));
    }

    @PatchMapping("/{versionId}/archive")
    @Operation(summary = "Archive a template version")
    public ApiResponse<ProjectTemplateVersionResponse> archive(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(archiveAction.execute(
                new ArchiveProjectTemplateVersionCommand(templateId, versionId)));
    }

    @PostMapping("/{versionId}/duplicate")
    @Operation(summary = "Duplicate a template version into a new DRAFT")
    public ResponseEntity<ApiResponse<ProjectTemplateVersionResponse>> duplicate(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                duplicateAction.execute(new DuplicateProjectTemplateVersionCommand(templateId, versionId))));
    }

    @PostMapping("/{versionId}/apply")
    @Operation(summary = "Apply a published template version to create a project")
    public ResponseEntity<ApiResponse<ProjectResponse>> apply(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody ApplyProjectTemplateRequest request) {
        boolean includeTasks = request.includeTemplateTasks() == null || request.includeTemplateTasks();
        boolean includeDeps = request.includeTemplateDependencies() == null || request.includeTemplateDependencies();
        boolean copyEstimates = request.copyEstimateHours() == null || request.copyEstimateHours();
        ProjectResponse response = applyAction.execute(new ApplyProjectTemplateCommand(
                templateId,
                versionId,
                request.workspaceId(),
                request.projectCode(),
                request.projectName(),
                request.projectDescription(),
                request.ownerUserId(),
                request.defaultCurrency(),
                request.plannedStartDate(),
                request.plannedEndDate(),
                includeTasks,
                includeDeps,
                copyEstimates));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
