package com.company.scopery.modules.governance.grant.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.grant.application.action.*;
import com.company.scopery.modules.governance.grant.application.command.*;
import com.company.scopery.modules.governance.grant.application.response.ObjectAccessGrantResponse;
import com.company.scopery.modules.governance.grant.application.service.AccessGrantQueryService;
import com.company.scopery.modules.governance.grant.http.request.CreateAccessGrantRequest;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(GovernanceApiPaths.PROJECT + "/access-grants") @Tag(name = "Governance - Access Grants")
public class ObjectAccessGrantController {
    private final CreateAccessGrantAction create; private final RevokeAccessGrantAction revoke; private final AccessGrantQueryService query;
    public ObjectAccessGrantController(CreateAccessGrantAction create, RevokeAccessGrantAction revoke, AccessGrantQueryService query) {
        this.create=create; this.revoke=revoke; this.query=query;
    }
    @PostMapping @Operation(summary="Create access grant")
    public ApiResponse<ObjectAccessGrantResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateAccessGrantRequest r) {
        return ApiResponse.success(create.execute(new CreateAccessGrantCommand(projectId, r.objectTypeCode(), r.targetId(), r.granteeType(), r.granteeId(), r.grantRole())));
    }
    @GetMapping @Operation(summary="List access grants")
    public ApiResponse<List<ObjectAccessGrantResponse>> list(@PathVariable UUID projectId, @RequestParam String objectTypeCode, @RequestParam UUID targetId) {
        return ApiResponse.success(query.list(projectId, objectTypeCode, targetId));
    }
    @PostMapping("/{grantId}/revoke") @Operation(summary="Revoke access grant")
    public ApiResponse<ObjectAccessGrantResponse> revoke(@PathVariable UUID projectId, @PathVariable UUID grantId) {
        return ApiResponse.success(revoke.execute(new RevokeAccessGrantCommand(projectId, grantId)));
    }
}
