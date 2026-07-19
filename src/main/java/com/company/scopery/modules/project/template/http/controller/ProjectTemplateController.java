package com.company.scopery.modules.project.template.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.template.application.action.ActivateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.ArchiveProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.CreateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.DeactivateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.action.UpdateProjectTemplateAction;
import com.company.scopery.modules.project.template.application.command.ActivateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.ArchiveProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.CreateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.DeactivateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.command.UpdateProjectTemplateCommand;
import com.company.scopery.modules.project.template.application.query.SearchProjectTemplateQuery;
import com.company.scopery.modules.project.template.application.response.ProjectTemplateResponse;
import com.company.scopery.modules.project.template.application.service.ProjectTemplateQueryService;
import com.company.scopery.modules.project.template.http.request.CreateProjectTemplateRequest;
import com.company.scopery.modules.project.template.http.request.UpdateProjectTemplateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_TEMPLATES)
@Tag(name = "Project - Templates")
public class ProjectTemplateController {

    private final CreateProjectTemplateAction createAction;
    private final UpdateProjectTemplateAction updateAction;
    private final ActivateProjectTemplateAction activateAction;
    private final DeactivateProjectTemplateAction deactivateAction;
    private final ArchiveProjectTemplateAction archiveAction;
    private final ProjectTemplateQueryService queryService;

    public ProjectTemplateController(CreateProjectTemplateAction createAction,
                                     UpdateProjectTemplateAction updateAction,
                                     ActivateProjectTemplateAction activateAction,
                                     DeactivateProjectTemplateAction deactivateAction,
                                     ArchiveProjectTemplateAction archiveAction,
                                     ProjectTemplateQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a project template")
    public ApiResponse<ProjectTemplateResponse> create(@Valid @RequestBody CreateProjectTemplateRequest request) {
        return ApiResponse.success(createAction.execute(new CreateProjectTemplateCommand(
                request.code(), request.name(), request.description(), request.scope(),
                request.organizationId(), request.workspaceId(), request.category(), request.visibility(),
                Boolean.TRUE.equals(request.builtIn())
        )));
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "Get a project template")
    public ApiResponse<ProjectTemplateResponse> get(@PathVariable UUID templateId) {
        return ApiResponse.success(queryService.getTemplate(templateId));
    }

    @GetMapping
    @Operation(summary = "Search project templates")
    public ApiResponse<PageResponse<ProjectTemplateResponse>> search(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<ProjectTemplateResponse> result = queryService.search(
                new SearchProjectTemplateQuery(scope, workspaceId, organizationId, status, category, keyword, page, size));
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{templateId}")
    @Operation(summary = "Update a project template")
    public ApiResponse<ProjectTemplateResponse> update(
            @PathVariable UUID templateId,
            @Valid @RequestBody UpdateProjectTemplateRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectTemplateCommand(
                templateId, request.name(), request.description(), request.category(), request.visibility())));
    }

    @PatchMapping("/{templateId}/activate")
    @Operation(summary = "Activate a project template")
    public ApiResponse<ProjectTemplateResponse> activate(@PathVariable UUID templateId) {
        return ApiResponse.success(activateAction.execute(new ActivateProjectTemplateCommand(templateId)));
    }

    @PatchMapping("/{templateId}/deactivate")
    @Operation(summary = "Deactivate a project template")
    public ApiResponse<ProjectTemplateResponse> deactivate(@PathVariable UUID templateId) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateProjectTemplateCommand(templateId)));
    }

    @PatchMapping("/{templateId}/archive")
    @Operation(summary = "Archive a project template")
    public ApiResponse<ProjectTemplateResponse> archive(@PathVariable UUID templateId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveProjectTemplateCommand(templateId)));
    }

}
