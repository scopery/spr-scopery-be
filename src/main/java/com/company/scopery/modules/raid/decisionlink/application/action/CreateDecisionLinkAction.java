package com.company.scopery.modules.raid.decisionlink.application.action;

import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.decisionlink.application.command.CreateDecisionLinkCommand;
import com.company.scopery.modules.raid.decisionlink.application.response.DecisionLinkResponse;
import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLink;
import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLinkRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDecisionLinkAction {
    private final DecisionRecordRepository decisions;
    private final DecisionLinkRepository links;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateDecisionLinkAction(DecisionRecordRepository decisions, DecisionLinkRepository links,
                                    RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.decisions = decisions;
        this.links = links;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionLinkResponse execute(CreateDecisionLinkCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        if (command.linkType() == null || command.linkType().isBlank()) {
            throw RaidExceptions.linkTypeRequired();
        }
        if (command.targetType() == null || command.targetType().isBlank()) {
            throw RaidExceptions.targetTypeRequired();
        }
        if (command.targetId() == null) {
            throw RaidExceptions.targetTypeRequired();
        }
        DecisionLink link = links.save(DecisionLink.create(
                command.decisionId(), command.projectId(), command.linkType().trim(),
                command.targetType().trim(), command.targetId()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION_LINK, link.id(), RaidActivityActions.DECISION_LINK_CREATED,
                "Decision link created");
        return DecisionLinkResponse.from(link);
    }
}
