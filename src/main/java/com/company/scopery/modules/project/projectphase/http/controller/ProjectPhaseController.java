package com.company.scopery.modules.project.projectphase.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.projectphase.application.action.*;
import com.company.scopery.modules.project.projectphase.application.command.*;
import com.company.scopery.modules.project.projectphase.application.query.SearchProjectPhaseQuery;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.application.service.ProjectPhaseQueryService;
import com.company.scopery.modules.project.projectphase.http.request.BulkCreateProjectPhaseRequest;
import com.company.scopery.modules.project.projectphase.http.request.CreateProjectPhaseFromDefinitionRequest;
import com.company.scopery.modules.project.projectphase.http.request.CreateProjectPhaseRequest;
import com.company.scopery.modules.project.projectphase.http.request.UpdateProjectPhaseRequest;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_PHASES)
@Tag(name = "Project - Project Phases")
public class ProjectPhaseController {

    private final ProjectPhaseQueryService queryService;
    private final CreateProjectPhaseAction createAction;
    private final CreateProjectPhaseFromDefinitionAction createFromDefinitionAction;
    private final UpdateProjectPhaseAction updateAction;
    private final ActivateProjectPhaseAction activateAction;
    private final CompleteProjectPhaseAction completeAction;
    private final ArchiveProjectPhaseAction archiveAction;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public ProjectPhaseController(ProjectPhaseQueryService queryService,
                                   CreateProjectPhaseAction createAction,
                                   CreateProjectPhaseFromDefinitionAction createFromDefinitionAction,
                                   UpdateProjectPhaseAction updateAction,
                                   ActivateProjectPhaseAction activateAction,
                                   CompleteProjectPhaseAction completeAction,
                                   ArchiveProjectPhaseAction archiveAction,
                                   BulkJobService bulkJobService,
                                   ObjectMapper objectMapper) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.createFromDefinitionAction = createFromDefinitionAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.completeAction = completeAction;
        this.archiveAction = archiveAction;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Create a project phase")
    public ApiResponse<ProjectPhaseResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectPhaseRequest request) {
        var cmd = new CreateProjectPhaseCommand(
                projectId,
                request.code(),
                request.name(),
                request.description(),
                request.displayOrder(),
                request.plannedStartDate(),
                request.plannedEndDate()
        );
        return ApiResponse.success(createAction.execute(cmd));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create project phases (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(
            @PathVariable UUID projectId,
            @Valid @RequestBody BulkCreateProjectPhaseRequest request) {
        var items = request.items().stream()
                .map(r -> new CreateProjectPhaseCommand(
                        projectId, r.code(), r.name(), r.description(),
                        r.displayOrder(), r.plannedStartDate(), r.plannedEndDate()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateProjectPhaseCommand(projectId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateProjectPhaseJobHandler.JOB_TYPE, request.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @PostMapping("/from-definition")
    @Operation(summary = "Create a project phase from a phase definition")
    public ApiResponse<ProjectPhaseResponse> createFromDefinition(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectPhaseFromDefinitionRequest request) {
        var cmd = new CreateProjectPhaseFromDefinitionCommand(
                projectId,
                request.phaseDefinitionId(),
                request.displayOrder(),
                request.plannedStartDate(),
                request.plannedEndDate()
        );
        return ApiResponse.success(createFromDefinitionAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project phase by ID")
    public ApiResponse<ProjectPhaseResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(queryService.getProjectPhase(projectId, id));
    }

    @GetMapping
    @Operation(summary = "Search project phases")
    public ApiResponse<PageResponse<ProjectPhaseResponse>> search(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchProjectPhaseQuery(projectId, status, page, size);
        PageResult<ProjectPhaseResponse> result = queryService.searchProjectPhases(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project phase")
    public ApiResponse<ProjectPhaseResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectPhaseRequest request) {
        var cmd = new UpdateProjectPhaseCommand(
                id,
                projectId,
                request.name(),
                request.description(),
                request.displayOrder(),
                request.plannedStartDate(),
                request.plannedEndDate()
        );
        return ApiResponse.success(updateAction.execute(cmd));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a project phase")
    public ApiResponse<ProjectPhaseResponse> activate(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(activateAction.execute(new ActivateProjectPhaseCommand(id, projectId)));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete a project phase")
    public ApiResponse<ProjectPhaseResponse> complete(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(completeAction.execute(new CompleteProjectPhaseCommand(id, projectId)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a project phase")
    public ApiResponse<ProjectPhaseResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(archiveAction.execute(new ArchiveProjectPhaseCommand(id, projectId)));
    }
}
