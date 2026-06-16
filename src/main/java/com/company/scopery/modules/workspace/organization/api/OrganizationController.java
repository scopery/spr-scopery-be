package com.company.scopery.modules.workspace.organization.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.organization.api.request.CreateOrganizationRequest;
import com.company.scopery.modules.workspace.organization.api.request.UpdateOrganizationRequest;
import com.company.scopery.modules.workspace.organization.application.OrganizationApplicationService;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.command.UpdateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.query.SearchOrganizationQuery;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
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

    private final OrganizationApplicationService organizationApplicationService;

    public OrganizationController(OrganizationApplicationService organizationApplicationService) {
        this.organizationApplicationService = organizationApplicationService;
    }

    @Operation(summary = "Create a new organization")
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request) {
        CreateOrganizationCommand command = new CreateOrganizationCommand(
                request.name(), request.code(), request.description());
        OrganizationResponse response = organizationApplicationService.createOrganization(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing organization")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        UpdateOrganizationCommand command = new UpdateOrganizationCommand(id, request.name(), request.description());
        OrganizationResponse response = organizationApplicationService.updateOrganization(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get organization detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable UUID id) {
        OrganizationResponse response = organizationApplicationService.getOrganization(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list organizations with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrganizationResponse>>> searchOrganizations(
            @Parameter(description = "Search by name or code") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by owner user ID") @RequestParam(required = false) UUID ownerUserId,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, ARCHIVED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchOrganizationQuery query = new SearchOrganizationQuery(keyword, ownerUserId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                organizationApplicationService.searchOrganizations(query))));
    }

    @Operation(summary = "Activate an organization")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<OrganizationResponse>> activateOrganization(@PathVariable UUID id) {
        OrganizationResponse response = organizationApplicationService.activateOrganization(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Archive an organization")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<OrganizationResponse>> archiveOrganization(@PathVariable UUID id) {
        OrganizationResponse response = organizationApplicationService.archiveOrganization(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
