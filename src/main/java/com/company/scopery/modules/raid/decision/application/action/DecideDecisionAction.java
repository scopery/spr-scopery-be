package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.decision.application.command.DecideDecisionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DecideDecisionAction {
    private final DecisionRecordRepository decisions;
    private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final RaidActivityLogger activityLogger;

    public DecideDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionRecordResponse execute(DecideDecisionCommand command) {
        authorization.requireDecide(command.projectId());
        if (command.outcome() == null || command.outcome().isBlank()) {
            throw RaidExceptions.outcomeRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var d = decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        d = decisions.save(d.decide(actor.id(), command.outcome().trim()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, d.id(), RaidActivityActions.DECISION_DECIDED, "Decision decided");
        return DecisionRecordResponse.from(d);
    }
}
