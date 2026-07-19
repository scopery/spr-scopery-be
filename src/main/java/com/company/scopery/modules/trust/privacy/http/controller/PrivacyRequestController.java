package com.company.scopery.modules.trust.privacy.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.privacy.application.action.*;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyRequestResponse;
import com.company.scopery.modules.trust.privacy.application.service.PrivacyRequestQueryService;
import com.company.scopery.modules.trust.privacy.http.request.*;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class PrivacyRequestController {
    private final CreatePrivacyRequestAction createAction;
    private final RejectPrivacyRequestAction rejectAction;
    private final TriagePrivacyRequestAction triageAction;
    private final MarkInReviewPrivacyRequestAction markInReviewAction;
    private final CompletePrivacyRequestAction completeAction;
    private final CancelPrivacyRequestAction cancelAction;
    private final PrivacyRequestQueryService queryService;
    public PrivacyRequestController(CreatePrivacyRequestAction createAction, RejectPrivacyRequestAction rejectAction,
            TriagePrivacyRequestAction triageAction, MarkInReviewPrivacyRequestAction markInReviewAction,
            CompletePrivacyRequestAction completeAction, CancelPrivacyRequestAction cancelAction,
            PrivacyRequestQueryService queryService) {
        this.createAction = createAction; this.rejectAction = rejectAction; this.triageAction = triageAction;
        this.markInReviewAction = markInReviewAction; this.completeAction = completeAction;
        this.cancelAction = cancelAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUESTS) @Operation(summary = "Create privacy request")
    public ApiResponse<PrivacyRequestResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreatePrivacyRequestRequest r) {
        return ApiResponse.success(createAction.execute(workspaceId, r));
    }
    @GetMapping(TrustApiPaths.PRIVACY_REQUESTS) @Operation(summary = "List privacy requests")
    public ApiResponse<List<PrivacyRequestResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @GetMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID) @Operation(summary = "Get privacy request")
    public ApiResponse<PrivacyRequestResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID requestId) {
        return ApiResponse.success(queryService.getById(workspaceId, requestId));
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID + "/triage") @Operation(summary = "Triage privacy request")
    public ApiResponse<PrivacyRequestResponse> triage(@PathVariable UUID workspaceId, @PathVariable UUID requestId) {
        return ApiResponse.success(triageAction.execute(workspaceId, requestId));
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID + "/mark-in-review") @Operation(summary = "Mark privacy request in review")
    public ApiResponse<PrivacyRequestResponse> markInReview(@PathVariable UUID workspaceId, @PathVariable UUID requestId) {
        return ApiResponse.success(markInReviewAction.execute(workspaceId, requestId));
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID + "/complete") @Operation(summary = "Complete privacy request")
    public ApiResponse<PrivacyRequestResponse> complete(@PathVariable UUID workspaceId, @PathVariable UUID requestId,
            @Valid @RequestBody CompletePrivacyRequestRequest r) {
        return ApiResponse.success(completeAction.execute(workspaceId, requestId, r.resolutionSummary()));
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID + "/reject") @Operation(summary = "Reject privacy request")
    public ApiResponse<PrivacyRequestResponse> reject(@PathVariable UUID workspaceId, @PathVariable UUID requestId,
            @Valid @RequestBody RejectPrivacyRequestRequest r) {
        return ApiResponse.success(rejectAction.execute(workspaceId, requestId, r.rejectionReason()));
    }
    @PostMapping(TrustApiPaths.PRIVACY_REQUEST_BY_ID + "/cancel") @Operation(summary = "Cancel privacy request")
    public ApiResponse<PrivacyRequestResponse> cancel(@PathVariable UUID workspaceId, @PathVariable UUID requestId) {
        return ApiResponse.success(cancelAction.execute(workspaceId, requestId));
    }
}
