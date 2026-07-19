package com.company.scopery.modules.scope.review.application.action;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.review.application.command.SubmitDeliverableReviewCommand;
import com.company.scopery.modules.scope.review.application.response.DeliverableReviewResponse;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReview;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReviewRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SubmitDeliverableReviewAction {
    private final DeliverableRepository deliverables;
    private final DeliverableReviewRepository reviews;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;
    public SubmitDeliverableReviewAction(DeliverableRepository deliverables, DeliverableReviewRepository reviews,
                                         ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables; this.reviews = reviews;
        this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public DeliverableReviewResponse execute(SubmitDeliverableReviewCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        var d = deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        if (reviews.findOpenByDeliverableId(command.deliverableId()).isPresent()) {
            throw ScopeExceptions.openReviewExists(command.deliverableId());
        }
        try {
            d = deliverables.save(d.inReview());
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        DeliverableReview review = reviews.save(DeliverableReview.submit(command.deliverableId(), command.projectId()));
        activityLogger.logSuccess(ScopeEntityTypes.REVIEW, review.id(), ScopeActivityActions.DELIVERABLE_REVIEW_SUBMITTED,
                "Deliverable review submitted");
        return DeliverableReviewResponse.from(review);
    }
}
