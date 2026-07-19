package com.company.scopery.modules.trust.retention.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.retention.application.action.CreateRetentionPolicyAction;
import com.company.scopery.modules.trust.retention.application.action.RunRetentionDryRunAction;
import com.company.scopery.modules.trust.retention.application.response.RetentionJobResponse;
import com.company.scopery.modules.trust.retention.application.response.RetentionPolicyResponse;
import com.company.scopery.modules.trust.retention.application.service.RetentionQueryService;
import com.company.scopery.modules.trust.retention.http.request.CreateRetentionPolicyRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class RetentionController {
    private final CreateRetentionPolicyAction createAction;
    private final RunRetentionDryRunAction dryRunAction;
    private final RetentionQueryService queryService;
    public RetentionController(CreateRetentionPolicyAction createAction, RunRetentionDryRunAction dryRunAction, RetentionQueryService queryService) {
        this.createAction = createAction; this.dryRunAction = dryRunAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.RETENTION_POLICIES) @Operation(summary = "Create retention policy")
    public ApiResponse<RetentionPolicyResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateRetentionPolicyRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.policyCode(), r.name(), r.objectTypeCode(), r.retentionPeriodDays(), r.retentionAction()));
    }
    @GetMapping(TrustApiPaths.RETENTION_POLICIES) @Operation(summary = "List retention policies")
    public ApiResponse<List<RetentionPolicyResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listPolicies(workspaceId));
    }
    @GetMapping(TrustApiPaths.RETENTION_POLICIES + "/{policyId}") @Operation(summary = "Get retention policy")
    public ApiResponse<RetentionPolicyResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID policyId) {
        return ApiResponse.success(queryService.getPolicy(workspaceId, policyId));
    }
    @PostMapping(TrustApiPaths.RETENTION_POLICY_DRY_RUN) @Operation(summary = "Run retention dry-run")
    public ApiResponse<RetentionJobResponse> dryRun(@PathVariable UUID workspaceId, @PathVariable UUID policyId,
            @RequestParam(defaultValue = "10") long simulatedCandidates) {
        return ApiResponse.success(dryRunAction.execute(workspaceId, policyId, simulatedCandidates));
    }
    @GetMapping(TrustApiPaths.RETENTION_JOBS) @Operation(summary = "List retention jobs")
    public ApiResponse<List<RetentionJobResponse>> listJobs(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listJobs(workspaceId));
    }
}
