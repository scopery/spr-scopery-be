package com.company.scopery.modules.scope.review.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.review.application.command.RejectReviewCommand;
import com.company.scopery.modules.scope.review.application.response.DeliverableReviewResponse;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReviewRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RejectReviewAction {
    private final DeliverableRepository deliverables;
    private final DeliverableReviewRepository reviews;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScopeActivityLogger activityLogger;
    public RejectReviewAction(DeliverableRepository deliverables, DeliverableReviewRepository reviews,
                              ScopeAuthorizationService authorization, CurrentUserAuthorizationService currentUser,
                              ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.reviews = reviews; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableReviewResponse execute(RejectReviewCommand command) {
        authorization.requireDeliverableAccept(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var review = reviews.findByIdAndProjectId(command.reviewId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.reviewNotFound(command.reviewId()));
        try {
            review = reviews.save(review.reject(actor.id(), command.reason()));
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.reviewInvalidStatus(ex.getMessage());
        }
        var deliverableId = review.deliverableId();
        var d = deliverables.findByIdAndProjectId(deliverableId, command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        deliverables.save(d.reject(actor.id(), command.reason()));
        activityLogger.logSuccess(ScopeEntityTypes.REVIEW, review.id(), ScopeActivityActions.DELIVERABLE_REVIEW_REJECTED,
                "Deliverable review rejected");
        return DeliverableReviewResponse.from(review);
    }
}
