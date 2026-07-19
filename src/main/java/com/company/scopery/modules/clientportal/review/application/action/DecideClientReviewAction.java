package com.company.scopery.modules.clientportal.review.application.action;
import com.company.scopery.modules.clientportal.review.application.command.DecideClientReviewCommand;
import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse;
import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewDecisionOutcome;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecision;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecisionRepository;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewRequestRepository;
import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalActivityActions;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalEntityTypes;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import com.company.scopery.modules.clientportal.shared.util.ClientPortalEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DecideClientReviewAction {
    private final ClientReviewRequestRepository repo;
    private final ClientReviewDecisionRepository decisionRepo;
    private final ClientPortalAuthorizationService authorization;
    private final ClientPortalActivityLogger activityLogger;
    public DecideClientReviewAction(ClientReviewRequestRepository repo, ClientReviewDecisionRepository decisionRepo,
                                    ClientPortalAuthorizationService authorization, ClientPortalActivityLogger activityLogger) {
        this.repo = repo; this.decisionRepo = decisionRepo;
        this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public ClientReviewRequestResponse execute(DecideClientReviewCommand c) {
        authorization.requireManage(c.projectId());
        var req = repo.findByIdAndProjectId(c.reviewId(), c.projectId()).orElseThrow(() -> ClientPortalExceptions.reviewNotFound(c.reviewId()));
        var outcome = ClientPortalEnumParser.parseRequired(ClientReviewDecisionOutcome.class, c.outcome(), "outcome");
        try {
            var saved = repo.save(req.decide());
            decisionRepo.save(ClientReviewDecision.create(saved.id(), saved.projectId(), outcome, c.comment(), null));
            activityLogger.logSuccess(ClientPortalEntityTypes.REVIEW_REQUEST, saved.id(), ClientPortalActivityActions.REVIEW_DECIDED, "Client review decided: " + outcome.name());
            return ClientReviewRequestResponse.from(saved);
        } catch (IllegalStateException ex) { throw ClientPortalExceptions.invalidStatus(ex.getMessage()); }
    }
}
