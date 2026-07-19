package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.raid.decision.application.command.RejectDecisionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RejectDecisionAction {
    private final DecisionRecordRepository decisions;
    private final RaidAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public RejectDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser) {
        this.decisions = decisions;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional
    public DecisionRecordResponse execute(RejectDecisionCommand command) {
        authorization.requireDecide(command.projectId());
        if (command.reason() == null || command.reason().isBlank()) {
            throw RaidExceptions.outcomeRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var d = decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        return DecisionRecordResponse.from(decisions.save(d.reject(actor.id(), command.reason().trim())));
    }
}
