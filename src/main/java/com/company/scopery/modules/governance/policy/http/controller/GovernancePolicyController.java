package com.company.scopery.modules.governance.policy.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.policy.application.action.UpsertGovernancePolicyAction;
import com.company.scopery.modules.governance.policy.application.command.UpsertGovernancePolicyCommand;
import com.company.scopery.modules.governance.policy.application.response.GovernancePolicyResponse;
import com.company.scopery.modules.governance.policy.application.service.GovernancePolicyQueryService;
import com.company.scopery.modules.governance.policy.http.request.UpsertGovernancePolicyRequest;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(GovernanceApiPaths.WS_POLICIES) @Tag(name = "Governance - Policies")
public class GovernancePolicyController {
    private final UpsertGovernancePolicyAction upsert; private final GovernancePolicyQueryService query;
    public GovernancePolicyController(UpsertGovernancePolicyAction upsert, GovernancePolicyQueryService query) { this.upsert=upsert; this.query=query; }
    @GetMapping @Operation(summary="List governance policies")
    public ApiResponse<List<GovernancePolicyResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @PutMapping @Operation(summary="Upsert governance policy")
    public ApiResponse<GovernancePolicyResponse> upsert(@PathVariable UUID workspaceId, @Valid @RequestBody UpsertGovernancePolicyRequest r) {
        return ApiResponse.success(upsert.execute(new UpsertGovernancePolicyCommand(
                workspaceId, r.objectTypeCode(), r.versioningMode(), r.versionOnUpdate(), r.lockOnFinalize(), r.allowRestore(), r.allowOwnerGrant(), r.baselineGuardMode(), r.auditLevel())));
    }
}
