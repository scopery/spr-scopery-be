package com.company.scopery.modules.scope.deliverable.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.application.command.AcceptDeliverableCommand;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptance;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptanceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AcceptDeliverableAction {
    private final DeliverableRepository deliverables;
    private final AcceptanceCriteriaRepository criteria;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final DeliverableAcceptanceRepository acceptances;
    private final ScopeActivityLogger activityLogger;

    public AcceptDeliverableAction(DeliverableRepository deliverables, AcceptanceCriteriaRepository criteria,
                                   ScopeAuthorizationService authorization, CurrentUserAuthorizationService currentUser,
                                   DeliverableAcceptanceRepository acceptances, ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables;
        this.criteria = criteria;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.acceptances = acceptances;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DeliverableResponse execute(AcceptDeliverableCommand command) {
        authorization.requireDeliverableAccept(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var d = deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        if (d.acceptanceRequired()) {
            boolean unmet = criteria.findByDeliverableId(command.deliverableId()).stream()
                    .anyMatch(c -> c.mandatory() && !c.isMet());
            if (unmet) {
                throw ScopeExceptions.criteriaNotMet(command.deliverableId());
            }
        }
        d = deliverables.save(d.accept(actor.id()));
        acceptances.save(DeliverableAcceptance.recordAccepted(command.deliverableId(), command.projectId(), actor.id(), null));
        activityLogger.logSuccess(ScopeEntityTypes.DELIVERABLE, d.id(), ScopeActivityActions.DELIVERABLE_ACCEPTED, "Deliverable accepted");
        return DeliverableResponse.from(d);
    }
}
