package com.company.scopery.modules.scope.deliverable.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.deliverable.application.command.ReopenDeliverableCommand;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReopenDeliverableAction {
    private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScopeActivityLogger activityLogger;

    public ReopenDeliverableAction(DeliverableRepository deliverables, ScopeAuthorizationService authorization,
                                   CurrentUserAuthorizationService currentUser, ScopeActivityLogger activityLogger) {
        this.deliverables = deliverables;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DeliverableResponse execute(ReopenDeliverableCommand command) {
        authorization.requireDeliverableReopen(command.projectId());
        if (command.reason() == null || command.reason().isBlank()) {
            throw ScopeExceptions.reopenReasonRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var d = deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        try {
            d = deliverables.save(d.reopen(actor.id(), command.reason().trim()));
        } catch (IllegalStateException ex) {
            throw ScopeExceptions.invalidStatus(ex.getMessage());
        }
        activityLogger.logSuccess(ScopeEntityTypes.DELIVERABLE, d.id(), ScopeActivityActions.DELIVERABLE_REOPENED, "Deliverable reopened");
        return DeliverableResponse.from(d);
    }
}
