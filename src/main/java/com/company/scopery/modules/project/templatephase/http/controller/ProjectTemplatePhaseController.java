package com.company.scopery.modules.project.templatephase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.templatephase.application.action.CreateProjectTemplatePhaseAction;
import com.company.scopery.modules.project.templatephase.application.action.DeleteProjectTemplatePhaseAction;
import com.company.scopery.modules.project.templatephase.application.action.ReorderProjectTemplatePhasesAction;
import com.company.scopery.modules.project.templatephase.application.action.UpdateProjectTemplatePhaseAction;
import com.company.scopery.modules.project.templatephase.application.command.CreateProjectTemplatePhaseCommand;
import com.company.scopery.modules.project.templatephase.application.command.DeleteProjectTemplatePhaseCommand;
import com.company.scopery.modules.project.templatephase.application.command.ReorderProjectTemplatePhasesCommand;
import com.company.scopery.modules.project.templatephase.application.command.UpdateProjectTemplatePhaseCommand;
import com.company.scopery.modules.project.templatephase.application.response.ProjectTemplatePhaseResponse;
import com.company.scopery.modules.project.templatephase.application.service.ProjectTemplatePhaseQueryService;
import com.company.scopery.modules.project.templatephase.http.request.CreateProjectTemplatePhaseRequest;
import com.company.scopery.modules.project.templatephase.http.request.ReorderProjectTemplatePhasesRequest;
import com.company.scopery.modules.project.templatephase.http.request.UpdateProjectTemplatePhaseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_TEMPLATE_PHASES)
@Tag(name = "Project - Template Phases")
public class ProjectTemplatePhaseController {

    private final CreateProjectTemplatePhaseAction createAction;
    private final UpdateProjectTemplatePhaseAction updateAction;
    private final DeleteProjectTemplatePhaseAction deleteAction;
    private final ReorderProjectTemplatePhasesAction reorderAction;
    private final ProjectTemplatePhaseQueryService queryService;

    public ProjectTemplatePhaseController(CreateProjectTemplatePhaseAction createAction,
                                          UpdateProjectTemplatePhaseAction updateAction,
                                          DeleteProjectTemplatePhaseAction deleteAction,
                                          ReorderProjectTemplatePhasesAction reorderAction,
                                          ProjectTemplatePhaseQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.reorderAction = reorderAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a template phase (DRAFT version only)")
    public ResponseEntity<ApiResponse<ProjectTemplatePhaseResponse>> create(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateProjectTemplatePhaseRequest request) {
        ProjectTemplatePhaseResponse response = createAction.execute(new CreateProjectTemplatePhaseCommand(
                templateId, versionId, request.phaseDefinitionId(), request.code(), request.name(),
                request.description(), request.displayOrder(), request.defaultDurationDays(),
                request.startOffsetDays(), request.deliverableDocumentTypeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List template phases for a version")
    public ApiResponse<List<ProjectTemplatePhaseResponse>> list(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.listPhases(templateId, versionId));
    }

    @GetMapping("/{phaseId}")
    @Operation(summary = "Get a template phase")
    public ApiResponse<ProjectTemplatePhaseResponse> get(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID phaseId) {
        return ApiResponse.success(queryService.getPhase(templateId, versionId, phaseId));
    }

    @PutMapping("/{phaseId}")
    @Operation(summary = "Update a template phase (DRAFT version only)")
    public ApiResponse<ProjectTemplatePhaseResponse> update(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID phaseId,
            @Valid @RequestBody UpdateProjectTemplatePhaseRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectTemplatePhaseCommand(
                templateId, versionId, phaseId, request.phaseDefinitionId(), request.code(),
                request.name(), request.description(), request.displayOrder(),
                request.defaultDurationDays(), request.startOffsetDays(),
                request.deliverableDocumentTypeId())));
    }

    @DeleteMapping("/{phaseId}")
    @Operation(summary = "Delete a template phase (DRAFT version only)")
    public ApiResponse<Void> delete(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID phaseId,
            @RequestParam(defaultValue = "false") boolean cascade) {
        deleteAction.execute(new DeleteProjectTemplatePhaseCommand(templateId, versionId, phaseId, cascade));
        return ApiResponse.success(null);
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder template phases (DRAFT version only)")
    public ApiResponse<List<ProjectTemplatePhaseResponse>> reorder(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody ReorderProjectTemplatePhasesRequest request) {
        return ApiResponse.success(reorderAction.execute(new ReorderProjectTemplatePhasesCommand(
                templateId, versionId, request.orderedPhaseIds())));
    }
}
