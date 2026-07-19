package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.UpdateDecisionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionStatus;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateDecisionAction {
    private final DecisionRecordRepository decisions;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public UpdateDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization,
                                RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionRecordResponse execute(UpdateDecisionCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        if (command.title() == null || command.title().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        if (command.rationale() == null || command.rationale().isBlank()) {
            throw RaidExceptions.rationaleRequired();
        }
        var d = decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        if (d.status() != DecisionStatus.PROPOSED && d.status() != DecisionStatus.UNDER_REVIEW) {
            throw RaidExceptions.decisionInvalidStatus("Decision can only be updated when PROPOSED or UNDER_REVIEW");
        }
        d = decisions.save(d.update(command.title().trim(), command.rationale().trim()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, d.id(), RaidActivityActions.DECISION_UPDATED, "Decision updated");
        return DecisionRecordResponse.from(d);
    }
}
