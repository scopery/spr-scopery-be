package com.company.scopery.modules.clientportal.policy.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.policy.application.action.CreatePermissionPolicyAction;
import com.company.scopery.modules.clientportal.policy.application.action.UpdatePermissionPolicyAction;
import com.company.scopery.modules.clientportal.policy.application.command.CreatePermissionPolicyCommand;
import com.company.scopery.modules.clientportal.policy.application.command.UpdatePermissionPolicyCommand;
import com.company.scopery.modules.clientportal.policy.application.response.ExternalPortalPermissionPolicyResponse;
import com.company.scopery.modules.clientportal.policy.application.service.ExternalPortalPermissionPolicyQueryService;
import com.company.scopery.modules.clientportal.policy.http.request.CreatePermissionPolicyRequest;
import com.company.scopery.modules.clientportal.policy.http.request.UpdatePermissionPolicyRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PERMISSION_POLICIES)
@Tag(name = "Client Portal - Permission Policies")
public class ExternalPortalPermissionPolicyController {
    private final CreatePermissionPolicyAction create;
    private final UpdatePermissionPolicyAction update;
    private final ExternalPortalPermissionPolicyQueryService query;
    public ExternalPortalPermissionPolicyController(CreatePermissionPolicyAction create, UpdatePermissionPolicyAction update,
                                                    ExternalPortalPermissionPolicyQueryService query) {
        this.create = create; this.update = update; this.query = query;
    }
    @PostMapping @Operation(summary = "Create permission policy")
    public ApiResponse<ExternalPortalPermissionPolicyResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreatePermissionPolicyRequest r) {
        return ApiResponse.success(create.execute(new CreatePermissionPolicyCommand(workspaceId, r.code(), r.name(), r.description(), r.permissionsJson())));
    }
    @GetMapping @Operation(summary = "List permission policies")
    public ApiResponse<List<ExternalPortalPermissionPolicyResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.list(workspaceId));
    }
    @GetMapping("/{policyId}") @Operation(summary = "Get permission policy")
    public ApiResponse<ExternalPortalPermissionPolicyResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID policyId) {
        return ApiResponse.success(query.get(workspaceId, policyId));
    }
    @PutMapping("/{policyId}") @Operation(summary = "Update permission policy")
    public ApiResponse<ExternalPortalPermissionPolicyResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID policyId,
                                                                      @Valid @RequestBody UpdatePermissionPolicyRequest r) {
        return ApiResponse.success(update.execute(new UpdatePermissionPolicyCommand(workspaceId, policyId, r.name(), r.description(), r.permissionsJson())));
    }
}
