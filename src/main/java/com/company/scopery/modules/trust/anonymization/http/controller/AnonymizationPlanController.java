package com.company.scopery.modules.trust.anonymization.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.anonymization.application.action.*;
import com.company.scopery.modules.trust.anonymization.application.response.AnonymizationPlanResponse;
import com.company.scopery.modules.trust.anonymization.application.service.AnonymizationPlanQueryService;
import com.company.scopery.modules.trust.anonymization.http.request.CreateAnonymizationPlanRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class AnonymizationPlanController {
    private final CreateAnonymizationPlanAction createAction;
    private final DryRunAnonymizationPlanAction dryRunAction;
    private final ExecuteAnonymizationPlanAction executeAction;
    private final CancelAnonymizationPlanAction cancelAction;
    private final AnonymizationPlanQueryService queryService;
    public AnonymizationPlanController(CreateAnonymizationPlanAction createAction, DryRunAnonymizationPlanAction dryRunAction,
            ExecuteAnonymizationPlanAction executeAction, CancelAnonymizationPlanAction cancelAction,
            AnonymizationPlanQueryService queryService) {
        this.createAction = createAction; this.dryRunAction = dryRunAction;
        this.executeAction = executeAction; this.cancelAction = cancelAction;
        this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.ANONYMIZATION_PLANS) @Operation(summary = "Create anonymization plan")
    public ApiResponse<AnonymizationPlanResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateAnonymizationPlanRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r.dataSubjectIndexId(), r.planJson(), r.reason()));
    }
    @GetMapping(TrustApiPaths.ANONYMIZATION_PLANS) @Operation(summary = "List anonymization plans")
    public ApiResponse<List<AnonymizationPlanResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @GetMapping(TrustApiPaths.ANONYMIZATION_PLANS + "/{planId}") @Operation(summary = "Get anonymization plan")
    public ApiResponse<AnonymizationPlanResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID planId) {
        return ApiResponse.success(queryService.getById(workspaceId, planId));
    }
    @PostMapping(TrustApiPaths.ANONYMIZATION_PLANS + "/{planId}/dry-run") @Operation(summary = "Run anonymization dry-run")
    public ApiResponse<AnonymizationPlanResponse> dryRun(@PathVariable UUID workspaceId, @PathVariable UUID planId) {
        return ApiResponse.success(dryRunAction.execute(workspaceId, planId));
    }
    @PostMapping(TrustApiPaths.ANONYMIZATION_PLANS + "/{planId}/execute") @Operation(summary = "Execute anonymization plan")
    public ApiResponse<AnonymizationPlanResponse> execute(@PathVariable UUID workspaceId, @PathVariable UUID planId) {
        return ApiResponse.success(executeAction.execute(workspaceId, planId));
    }
    @PostMapping(TrustApiPaths.ANONYMIZATION_PLANS + "/{planId}/cancel") @Operation(summary = "Cancel anonymization plan")
    public ApiResponse<AnonymizationPlanResponse> cancel(@PathVariable UUID workspaceId, @PathVariable UUID planId) {
        return ApiResponse.success(cancelAction.execute(workspaceId, planId));
    }
}
