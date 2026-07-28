package com.company.scopery.modules.iam.authorization.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.authorization.application.response.CapabilitiesResponse;
import com.company.scopery.modules.iam.authorization.application.service.NavCapabilitiesQueryService;
import com.company.scopery.modules.iam.authorization.domain.enums.NavCapabilityPack;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "IAM - Nav Capabilities", description = "Nav capability packs for FE tab visibility")
@RestController
public class NavCapabilitiesController {

    private final NavCapabilitiesQueryService queryService;

    public NavCapabilitiesController(NavCapabilitiesQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Evaluate nav capability pack on a workspace")
    @GetMapping(WorkspaceApiPaths.WORKSPACE_CAPABILITIES)
    public ApiResponse<CapabilitiesResponse> workspaceCapabilities(
            @PathVariable UUID workspaceId,
            @RequestParam String pack,
            @RequestParam(required = false) UUID projectId) {
        return ApiResponse.success(
                queryService.forWorkspace(workspaceId, NavCapabilityPack.fromParam(pack), projectId));
    }

    @Operation(summary = "Evaluate nav capability pack on an organization")
    @GetMapping(WorkspaceApiPaths.ORGANIZATION_CAPABILITIES)
    public ApiResponse<CapabilitiesResponse> organizationCapabilities(
            @PathVariable UUID organizationId,
            @RequestParam String pack) {
        return ApiResponse.success(queryService.forOrganization(organizationId, NavCapabilityPack.fromParam(pack)));
    }
}
