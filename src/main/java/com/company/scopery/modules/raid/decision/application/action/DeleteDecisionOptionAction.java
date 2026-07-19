package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.DeleteDecisionOptionCommand;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteDecisionOptionAction {
    private final DecisionRecordRepository decisions;
    private final DecisionOptionRepository options;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public DeleteDecisionOptionAction(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                      RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.options = options;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteDecisionOptionCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        var option = options.findById(command.optionId())
                .orElseThrow(() -> RaidExceptions.decisionOptionNotFound(command.optionId()));
        if (!option.decisionId().equals(command.decisionId()) || !option.projectId().equals(command.projectId())) {
            throw RaidExceptions.decisionOptionNotFound(command.optionId());
        }
        options.deleteById(command.optionId());
        activityLogger.logSuccess(RaidEntityTypes.OPTION, command.optionId(), RaidActivityActions.DECISION_OPTION_DELETED,
                "Decision option deleted");
    }
}
