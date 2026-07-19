package com.company.scopery.modules.scope.review.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.review.application.action.ApproveReviewAction;
import com.company.scopery.modules.scope.review.application.action.RejectReviewAction;
import com.company.scopery.modules.scope.review.application.action.RequestReworkAction;
import com.company.scopery.modules.scope.review.application.action.SubmitDeliverableReviewAction;
import com.company.scopery.modules.scope.review.application.command.ApproveReviewCommand;
import com.company.scopery.modules.scope.review.application.command.RejectReviewCommand;
import com.company.scopery.modules.scope.review.application.command.RequestReworkCommand;
import com.company.scopery.modules.scope.review.application.command.SubmitDeliverableReviewCommand;
import com.company.scopery.modules.scope.review.application.response.DeliverableReviewResponse;
import com.company.scopery.modules.scope.review.http.request.ReviewDecisionRequest;
import com.company.scopery.modules.scope.review.http.request.ReviewReasonRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@Tag(name = "Scope - Reviews")
public class ReviewController {
    private final SubmitDeliverableReviewAction submit;
    private final ApproveReviewAction approve;
    private final RejectReviewAction reject;
    private final RequestReworkAction requestRework;
    public ReviewController(SubmitDeliverableReviewAction submit, ApproveReviewAction approve,
                            RejectReviewAction reject, RequestReworkAction requestRework) {
        this.submit = submit; this.approve = approve; this.reject = reject; this.requestRework = requestRework;
    }
    @PostMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/submit-review")
    @Operation(summary = "Submit deliverable for review")
    public ApiResponse<DeliverableReviewResponse> submit(@PathVariable UUID projectId, @PathVariable UUID deliverableId) {
        return ApiResponse.success(submit.execute(new SubmitDeliverableReviewCommand(projectId, deliverableId)));
    }
    @PostMapping(ScopeApiPaths.REVIEWS + "/{reviewId}/approve")
    @Operation(summary = "Approve deliverable review")
    public ApiResponse<DeliverableReviewResponse> approve(@PathVariable UUID projectId, @PathVariable UUID reviewId,
                                                          @RequestBody(required = false) ReviewDecisionRequest request) {
        String decision = request == null ? null : request.decision();
        return ApiResponse.success(approve.execute(new ApproveReviewCommand(projectId, reviewId, decision)));
    }
    @PostMapping(ScopeApiPaths.REVIEWS + "/{reviewId}/reject")
    @Operation(summary = "Reject deliverable review")
    public ApiResponse<DeliverableReviewResponse> reject(@PathVariable UUID projectId, @PathVariable UUID reviewId,
                                                         @RequestBody(required = false) ReviewReasonRequest request) {
        String reason = request == null ? null : request.reason();
        return ApiResponse.success(reject.execute(new RejectReviewCommand(projectId, reviewId, reason)));
    }
    @PostMapping(ScopeApiPaths.REVIEWS + "/{reviewId}/request-rework")
    @Operation(summary = "Request deliverable rework")
    public ApiResponse<DeliverableReviewResponse> requestRework(@PathVariable UUID projectId, @PathVariable UUID reviewId,
                                                                @RequestBody(required = false) ReviewReasonRequest request) {
        String reason = request == null ? null : request.reason();
        return ApiResponse.success(requestRework.execute(new RequestReworkCommand(projectId, reviewId, reason)));
    }
}
