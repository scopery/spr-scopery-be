package com.company.scopery.modules.iam.permission.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.permission.application.query.SearchIamPermissionQuery;
import com.company.scopery.modules.iam.permission.application.response.IamPermissionActionResponse;
import com.company.scopery.modules.iam.permission.application.response.IamPermissionResponse;
import com.company.scopery.modules.iam.permission.application.service.IamPermissionQueryService;
import com.company.scopery.modules.iam.permission.http.request.SearchIamPermissionRequest;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "IAM - Permissions", description = "View permission/action catalog")
@RestController
@RequestMapping(IamApiPaths.IAM_PERMISSIONS)
public class IamPermissionController {

    private final IamPermissionQueryService queryService;

    public IamPermissionController(IamPermissionQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Get active permission/action matrix")
    @GetMapping("/matrix")
    public ResponseEntity<ApiResponse<List<IamPermissionResponse>>> getPermissionMatrix() {
        return ResponseEntity.ok(ApiResponse.success(queryService.getPermissionMatrix()));
    }

    @Operation(summary = "Search permissions")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamPermissionResponse>>> searchPermissions(
            @ModelAttribute SearchIamPermissionRequest request) {
        PageResult<IamPermissionResponse> result = queryService.searchPermissions(new SearchIamPermissionQuery(
                request.keyword(), request.moduleCode(), request.resourceScopeLevel(),
                request.dataAccessPolicy(), request.permissionCategory(), request.riskLevel(),
                request.assignableSubjectType(), request.status(), request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Get permission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamPermissionResponse>> getPermission(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getPermission(id)));
    }

    @Operation(summary = "List actions under a permission")
    @GetMapping("/{id}/actions")
    public ResponseEntity<ApiResponse<List<IamPermissionActionResponse>>> getPermissionActions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getPermissionActions(id)));
    }
}
