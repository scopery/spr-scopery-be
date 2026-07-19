package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.CreateDecisionOptionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionOptionResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOption;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDecisionOptionAction {
    private final DecisionRecordRepository decisions;
    private final DecisionOptionRepository options;
    private final RaidAuthorizationService authorization;

    public CreateDecisionOptionAction(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                      RaidAuthorizationService authorization) {
        this.decisions = decisions;
        this.options = options;
        this.authorization = authorization;
    }

    @Transactional
    public DecisionOptionResponse execute(CreateDecisionOptionCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        if (command.optionTitle() == null || command.optionTitle().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        DecisionOption o = options.save(DecisionOption.create(
                command.decisionId(), command.projectId(), command.optionTitle().trim(),
                command.optionDescription(), command.pros(), command.cons(), command.estimatedImpact()));
        return DecisionOptionResponse.from(o);
    }
}
