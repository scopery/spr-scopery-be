package com.company.scopery.modules.trust.accessreview.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.accessreview.application.action.*;
import com.company.scopery.modules.trust.accessreview.application.response.AccessReviewCampaignResponse;
import com.company.scopery.modules.trust.accessreview.application.response.PermissionReviewFindingResponse;
import com.company.scopery.modules.trust.accessreview.application.service.AccessReviewQueryService;
import com.company.scopery.modules.trust.accessreview.http.request.CreateAccessReviewCampaignRequest;
import com.company.scopery.modules.trust.accessreview.http.request.CreatePermissionReviewFindingRequest;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class AccessReviewController {
    private final CreateAccessReviewCampaignAction createCampaign;
    private final StartAccessReviewCampaignAction startCampaign;
    private final CompleteAccessReviewCampaignAction completeCampaign;
    private final CancelAccessReviewCampaignAction cancelCampaign;
    private final CreatePermissionReviewFindingAction createFinding;
    private final ResolvePermissionReviewFindingAction resolveFinding;
    private final DismissPermissionReviewFindingAction dismissFinding;
    private final AccessReviewQueryService queryService;
    public AccessReviewController(CreateAccessReviewCampaignAction createCampaign, StartAccessReviewCampaignAction startCampaign,
            CompleteAccessReviewCampaignAction completeCampaign, CancelAccessReviewCampaignAction cancelCampaign,
            CreatePermissionReviewFindingAction createFinding, ResolvePermissionReviewFindingAction resolveFinding,
            DismissPermissionReviewFindingAction dismissFinding, AccessReviewQueryService queryService) {
        this.createCampaign = createCampaign; this.startCampaign = startCampaign;
        this.completeCampaign = completeCampaign; this.cancelCampaign = cancelCampaign;
        this.createFinding = createFinding; this.resolveFinding = resolveFinding;
        this.dismissFinding = dismissFinding; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS) @Operation(summary = "Create access review campaign")
    public ApiResponse<AccessReviewCampaignResponse> createCampaign(@PathVariable UUID workspaceId, @Valid @RequestBody CreateAccessReviewCampaignRequest r) {
        return ApiResponse.success(createCampaign.execute(workspaceId, r.name(), r.scopeJson()));
    }
    @GetMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS) @Operation(summary = "List access review campaigns")
    public ApiResponse<List<AccessReviewCampaignResponse>> listCampaigns(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listCampaigns(workspaceId));
    }
    @GetMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS + "/{campaignId}") @Operation(summary = "Get access review campaign")
    public ApiResponse<AccessReviewCampaignResponse> getCampaign(@PathVariable UUID workspaceId, @PathVariable UUID campaignId) {
        return ApiResponse.success(queryService.getCampaign(workspaceId, campaignId));
    }
    @PostMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS + "/{campaignId}/start") @Operation(summary = "Start access review campaign")
    public ApiResponse<AccessReviewCampaignResponse> start(@PathVariable UUID workspaceId, @PathVariable UUID campaignId) {
        return ApiResponse.success(startCampaign.execute(workspaceId, campaignId));
    }
    @PostMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS + "/{campaignId}/complete") @Operation(summary = "Complete access review campaign")
    public ApiResponse<AccessReviewCampaignResponse> complete(@PathVariable UUID workspaceId, @PathVariable UUID campaignId) {
        return ApiResponse.success(completeCampaign.execute(workspaceId, campaignId));
    }
    @PostMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS + "/{campaignId}/cancel") @Operation(summary = "Cancel access review campaign")
    public ApiResponse<AccessReviewCampaignResponse> cancel(@PathVariable UUID workspaceId, @PathVariable UUID campaignId) {
        return ApiResponse.success(cancelCampaign.execute(workspaceId, campaignId));
    }
    @PostMapping(TrustApiPaths.ACCESS_REVIEW_CAMPAIGNS + "/{campaignId}/findings") @Operation(summary = "Create permission review finding")
    public ApiResponse<PermissionReviewFindingResponse> createFinding(@PathVariable UUID workspaceId, @PathVariable UUID campaignId,
            @Valid @RequestBody CreatePermissionReviewFindingRequest r) {
        return ApiResponse.success(createFinding.execute(workspaceId, campaignId, r.findingType(), r.severity(), r.recommendation()));
    }
    @GetMapping(TrustApiPaths.PERMISSION_FINDINGS) @Operation(summary = "List permission review findings")
    public ApiResponse<List<PermissionReviewFindingResponse>> listFindings(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listFindings(workspaceId));
    }
    @PostMapping(TrustApiPaths.PERMISSION_FINDINGS + "/{findingId}/resolve") @Operation(summary = "Resolve permission review finding")
    public ApiResponse<PermissionReviewFindingResponse> resolve(@PathVariable UUID workspaceId, @PathVariable UUID findingId) {
        return ApiResponse.success(resolveFinding.execute(workspaceId, findingId));
    }
    @PostMapping(TrustApiPaths.PERMISSION_FINDINGS + "/{findingId}/dismiss") @Operation(summary = "Dismiss permission review finding")
    public ApiResponse<PermissionReviewFindingResponse> dismiss(@PathVariable UUID workspaceId, @PathVariable UUID findingId) {
        return ApiResponse.success(dismissFinding.execute(workspaceId, findingId));
    }
}
