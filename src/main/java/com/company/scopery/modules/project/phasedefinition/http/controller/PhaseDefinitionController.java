package com.company.scopery.modules.project.phasedefinition.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.phasedefinition.application.action.ArchivePhaseDefinitionAction;
import com.company.scopery.modules.project.phasedefinition.application.action.CreateSystemPhaseDefinitionAction;
import com.company.scopery.modules.project.phasedefinition.application.action.CreateWorkspacePhaseDefinitionAction;
import com.company.scopery.modules.project.phasedefinition.application.action.UpdatePhaseDefinitionAction;
import com.company.scopery.modules.project.phasedefinition.application.command.ArchivePhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.command.CreateSystemPhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.command.CreateWorkspacePhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.command.UpdatePhaseDefinitionCommand;
import com.company.scopery.modules.project.phasedefinition.application.query.SearchPhaseDefinitionQuery;
import com.company.scopery.modules.project.phasedefinition.application.response.PhaseDefinitionResponse;
import com.company.scopery.modules.project.phasedefinition.application.service.PhaseDefinitionQueryService;
import com.company.scopery.modules.project.phasedefinition.http.request.CreateSystemPhaseDefinitionRequest;
import com.company.scopery.modules.project.phasedefinition.http.request.CreateWorkspacePhaseDefinitionRequest;
import com.company.scopery.modules.project.phasedefinition.http.request.UpdatePhaseDefinitionRequest;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PHASE_DEFINITIONS)
@Tag(name = "Project - Phase Definitions")
public class PhaseDefinitionController {

    private final CreateSystemPhaseDefinitionAction createSystemPhaseDefinitionAction;
    private final CreateWorkspacePhaseDefinitionAction createWorkspacePhaseDefinitionAction;
    private final UpdatePhaseDefinitionAction updatePhaseDefinitionAction;
    private final ArchivePhaseDefinitionAction archivePhaseDefinitionAction;
    private final PhaseDefinitionQueryService queryService;

    public PhaseDefinitionController(
            CreateSystemPhaseDefinitionAction createSystemPhaseDefinitionAction,
            CreateWorkspacePhaseDefinitionAction createWorkspacePhaseDefinitionAction,
            UpdatePhaseDefinitionAction updatePhaseDefinitionAction,
            ArchivePhaseDefinitionAction archivePhaseDefinitionAction,
            PhaseDefinitionQueryService queryService) {
        this.createSystemPhaseDefinitionAction = createSystemPhaseDefinitionAction;
        this.createWorkspacePhaseDefinitionAction = createWorkspacePhaseDefinitionAction;
        this.updatePhaseDefinitionAction = updatePhaseDefinitionAction;
        this.archivePhaseDefinitionAction = archivePhaseDefinitionAction;
        this.queryService = queryService;
    }

    @PostMapping("/system")
    @Operation(summary = "Create a system-scoped phase definition")
    public ApiResponse<PhaseDefinitionResponse> createSystemPhaseDefinition(
            @Valid @RequestBody CreateSystemPhaseDefinitionRequest request) {
        PhaseDefinitionResponse response = createSystemPhaseDefinitionAction.execute(
                new CreateSystemPhaseDefinitionCommand(
                        request.code(),
                        request.name(),
                        request.description(),
                        request.displayOrder(),
                        request.isSystemDefault()
                )
        );
        return ApiResponse.success(response);
    }

    @PostMapping
    @Operation(summary = "Create a workspace-scoped phase definition")
    public ApiResponse<PhaseDefinitionResponse> createWorkspacePhaseDefinition(
            @RequestParam UUID workspaceId,
            @Valid @RequestBody CreateWorkspacePhaseDefinitionRequest request) {
        PhaseDefinitionResponse response = createWorkspacePhaseDefinitionAction.execute(
                new CreateWorkspacePhaseDefinitionCommand(
                        workspaceId,
                        request.code(),
                        request.name(),
                        request.description(),
                        request.displayOrder()
                )
        );
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a phase definition by ID")
    public ApiResponse<PhaseDefinitionResponse> getPhaseDefinition(@PathVariable UUID id) {
        return ApiResponse.success(queryService.getPhaseDefinition(id));
    }

    @GetMapping
    @Operation(summary = "Search phase definitions")
    public ApiResponse<PageResponse<PhaseDefinitionResponse>> searchPhaseDefinitions(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<PhaseDefinitionResponse> result = queryService.searchPhaseDefinitions(
                new SearchPhaseDefinitionQuery(scope, workspaceId, keyword, status, page, size)
        );
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a phase definition")
    public ApiResponse<PhaseDefinitionResponse> updatePhaseDefinition(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePhaseDefinitionRequest request) {
        PhaseDefinitionResponse response = updatePhaseDefinitionAction.execute(
                new UpdatePhaseDefinitionCommand(id, request.name(), request.description(), request.displayOrder())
        );
        return ApiResponse.success(response);
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a phase definition")
    public ApiResponse<PhaseDefinitionResponse> archivePhaseDefinition(@PathVariable UUID id) {
        return ApiResponse.success(archivePhaseDefinitionAction.execute(new ArchivePhaseDefinitionCommand(id)));
    }
}
