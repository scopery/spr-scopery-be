package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.CreateDecisionCommand;
import com.company.scopery.modules.raid.decision.application.command.SupersedeDecisionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionStatus;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class SupersedeDecisionAction {
    private final DecisionRecordRepository decisions;
    private final CreateDecisionAction createDecisionAction;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public SupersedeDecisionAction(DecisionRecordRepository decisions,
                                   CreateDecisionAction createDecisionAction,
                                   RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.createDecisionAction = createDecisionAction;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionRecordResponse execute(SupersedeDecisionCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        var original = decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        if (original.status() == DecisionStatus.SUPERSEDED || original.status() == DecisionStatus.ARCHIVED) {
            throw RaidExceptions.decisionInvalidStatus("Decision cannot be superseded in status " + original.status());
        }

        UUID replacementId = resolveReplacementId(command, original);
        var replacement = decisions.findByIdAndProjectId(replacementId, command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(replacementId));
        if (replacement.id().equals(original.id())) {
            throw RaidExceptions.decisionInvalidStatus("Replacement decision must differ from original");
        }

        var superseded = decisions.save(original.supersede(replacementId));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, superseded.id(), RaidActivityActions.DECISION_SUPERSEDED,
                "Decision superseded by " + replacementId);
        return DecisionRecordResponse.from(superseded);
    }

    private UUID resolveReplacementId(SupersedeDecisionCommand command, DecisionRecord original) {
        if (command.replacementDecisionId() != null) {
            return command.replacementDecisionId();
        }
        if (command.title() != null && !command.title().isBlank()
                && command.rationale() != null && !command.rationale().isBlank()) {
            String category = command.category() != null && !command.category().isBlank()
                    ? command.category() : original.category().name();
            var created = createDecisionAction.execute(new CreateDecisionCommand(
                    command.projectId(), command.title().trim(), command.rationale().trim(), category, null));
            return created.id();
        }
        throw RaidExceptions.decisionReplacementRequired();
    }
}
