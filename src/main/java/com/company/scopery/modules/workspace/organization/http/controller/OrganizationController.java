package com.company.scopery.modules.workspace.organization.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.organization.application.action.ActivateOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.action.ArchiveOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.action.CreateOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.action.UpdateOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.command.UpdateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.query.SearchOrganizationQuery;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.application.service.OrganizationQueryService;
import com.company.scopery.modules.workspace.organization.http.request.CreateOrganizationRequest;
import com.company.scopery.modules.workspace.organization.http.request.UpdateOrganizationRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Organizations", description = "Manage organizations")
@RestController
@RequestMapping(WorkspaceApiPaths.ORGANIZATIONS)
public class OrganizationController {

    private final CreateOrganizationAction createAction;
    private final UpdateOrganizationAction updateAction;
    private final ActivateOrganizationAction activateAction;
    private final ArchiveOrganizationAction archiveAction;
    private final OrganizationQueryService queryService;

    public OrganizationController(CreateOrganizationAction createAction,
                                   UpdateOrganizationAction updateAction,
                                   ActivateOrganizationAction activateAction,
                                   ArchiveOrganizationAction archiveAction,
                                   OrganizationQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.archiveAction = archiveAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new organization")
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createAction.execute(new CreateOrganizationCommand(
                        request.name(), request.code(), request.description()))));
    }

    @Operation(summary = "Update an existing organization")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateAction.execute(new UpdateOrganizationCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get organization detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getOrganization(id)));
    }

    @Operation(summary = "Search and list organizations with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrganizationResponse>>> searchOrganizations(
            @Parameter(description = "Search by name or code") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by owner user ID") @RequestParam(required = false) UUID ownerUserId,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, ARCHIVED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrganizationResponse> result =
                queryService.searchOrganizations(new SearchOrganizationQuery(keyword, ownerUserId, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate an organization")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<OrganizationResponse>> activateOrganization(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(id)));
    }

    @Operation(summary = "Archive an organization")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<OrganizationResponse>> archiveOrganization(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(archiveAction.execute(id)));
    }
}
