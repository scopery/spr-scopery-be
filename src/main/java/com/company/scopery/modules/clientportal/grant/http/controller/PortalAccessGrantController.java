package com.company.scopery.modules.clientportal.grant.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.grant.application.action.*;
import com.company.scopery.modules.clientportal.grant.application.command.CreatePortalAccessGrantCommand;
import com.company.scopery.modules.clientportal.grant.application.command.RevokePortalAccessGrantCommand;
import com.company.scopery.modules.clientportal.grant.application.response.ExternalProjectAccessGrantResponse;
import com.company.scopery.modules.clientportal.grant.application.service.PortalAccessGrantQueryService;
import com.company.scopery.modules.clientportal.grant.http.request.CreatePortalAccessGrantRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.ACCESS_GRANTS)
@Tag(name = "Client Portal - Access Grants")
public class PortalAccessGrantController {
    private final CreatePortalAccessGrantAction create;
    private final RevokePortalAccessGrantAction revoke;
    private final PortalAccessGrantQueryService query;
    public PortalAccessGrantController(CreatePortalAccessGrantAction create, RevokePortalAccessGrantAction revoke, PortalAccessGrantQueryService query) {
        this.create=create; this.revoke=revoke; this.query=query;
    }
    @PostMapping @Operation(summary = "Create access grant")
    public ApiResponse<ExternalProjectAccessGrantResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreatePortalAccessGrantRequest r) {
        return ApiResponse.success(create.execute(new CreatePortalAccessGrantCommand(projectId, r.portalAccountId(), r.permissionPolicyCode(), r.expiresAt())));
    }
    @GetMapping @Operation(summary = "List access grants")
    public ApiResponse<List<ExternalProjectAccessGrantResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @PostMapping("/{grantId}/revoke") @Operation(summary = "Revoke access grant")
    public ApiResponse<ExternalProjectAccessGrantResponse> revoke(@PathVariable UUID projectId, @PathVariable UUID grantId) {
        return ApiResponse.success(revoke.execute(new RevokePortalAccessGrantCommand(projectId, grantId)));
    }
}
