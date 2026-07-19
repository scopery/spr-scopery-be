package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.raid.decision.application.command.CreateDecisionCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionCategory;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDecisionAction {
    private final ProjectRepository projects;
    private final DecisionRecordRepository decisions;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateDecisionAction(ProjectRepository projects, DecisionRecordRepository decisions,
                                RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.projects = projects;
        this.decisions = decisions;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DecisionRecordResponse execute(CreateDecisionCommand command) {
        authorization.requireDecisionCreate(command.projectId());
        var project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw RaidExceptions.projectArchived(command.projectId());
        }
        if (command.title() == null || command.title().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        if (command.rationale() == null || command.rationale().isBlank()) {
            throw RaidExceptions.rationaleRequired();
        }
        DecisionCategory category = RaidEnumParser.parseRequired(DecisionCategory.class, command.category(), "category");
        DecisionRecord d = decisions.save(DecisionRecord.create(
                project.id(), project.workspaceId(), command.code(), command.title().trim(), category, command.rationale().trim()));
        activityLogger.logSuccess(RaidEntityTypes.DECISION, d.id(), RaidActivityActions.DECISION_CREATED, "Decision created");
        return DecisionRecordResponse.from(d);
    }
}
