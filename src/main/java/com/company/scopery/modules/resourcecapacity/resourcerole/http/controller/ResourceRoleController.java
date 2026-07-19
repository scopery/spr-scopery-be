package com.company.scopery.modules.resourcecapacity.resourcerole.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.resourcerole.application.action.CreateResourceRoleAction;
import com.company.scopery.modules.resourcecapacity.resourcerole.application.response.ResourceRoleResponse;
import com.company.scopery.modules.resourcecapacity.resourcerole.application.service.ResourceRoleQueryService;
import com.company.scopery.modules.resourcecapacity.resourcerole.http.request.CreateResourceRoleRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - ResourceRoles")
public class ResourceRoleController {
    private final CreateResourceRoleAction create; private final ResourceRoleQueryService query;
    public ResourceRoleController(CreateResourceRoleAction create, ResourceRoleQueryService query) { this.create=create; this.query=query; }
    @PostMapping(CapacityApiPaths.ROLES) @Operation(summary="Create ResourceRole")
    public ApiResponse<ResourceRoleResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateResourceRoleRequest r) {
        return ApiResponse.success(create.execute(workspaceId, r));
    }
    @GetMapping(CapacityApiPaths.ROLES) @Operation(summary="List ResourceRoles")
    public ApiResponse<List<ResourceRoleResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
}
