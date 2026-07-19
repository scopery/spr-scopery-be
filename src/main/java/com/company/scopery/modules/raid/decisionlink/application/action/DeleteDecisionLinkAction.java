package com.company.scopery.modules.raid.decisionlink.application.action;

import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.decisionlink.application.command.DeleteDecisionLinkCommand;
import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLinkRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteDecisionLinkAction {
    private final DecisionRecordRepository decisions;
    private final DecisionLinkRepository links;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public DeleteDecisionLinkAction(DecisionRecordRepository decisions, DecisionLinkRepository links,
                                    RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.links = links;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteDecisionLinkCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        var link = links.findByIdAndProjectId(command.linkId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionLinkNotFound(command.linkId()));
        if (!link.decisionId().equals(command.decisionId())) {
            throw RaidExceptions.decisionLinkNotFound(command.linkId());
        }
        links.deleteByIdAndProjectId(command.linkId(), command.projectId());
        activityLogger.logSuccess(RaidEntityTypes.DECISION_LINK, command.linkId(), RaidActivityActions.DECISION_LINK_DELETED,
                "Decision link deleted");
    }
}
