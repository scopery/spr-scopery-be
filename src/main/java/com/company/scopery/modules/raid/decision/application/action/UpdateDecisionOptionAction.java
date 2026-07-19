package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.UpdateDecisionOptionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionOptionResponse;
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
public class UpdateDecisionOptionAction {
    private final DecisionRecordRepository decisions;
    private final DecisionOptionRepository options;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public UpdateDecisionOptionAction(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                      RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.options = options;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionOptionResponse execute(UpdateDecisionOptionCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        if (command.optionTitle() == null || command.optionTitle().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        var option = options.findById(command.optionId())
                .orElseThrow(() -> RaidExceptions.decisionOptionNotFound(command.optionId()));
        if (!option.decisionId().equals(command.decisionId()) || !option.projectId().equals(command.projectId())) {
            throw RaidExceptions.decisionOptionNotFound(command.optionId());
        }
        option = options.save(option.update(
                command.optionTitle().trim(), command.optionDescription(), command.pros(),
                command.cons(), command.estimatedImpact()));
        activityLogger.logSuccess(RaidEntityTypes.OPTION, option.id(), RaidActivityActions.DECISION_OPTION_UPDATED,
                "Decision option updated");
        return DecisionOptionResponse.from(option);
    }
}
