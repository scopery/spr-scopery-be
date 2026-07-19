package com.company.scopery.modules.raid.raiditem.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.raid.raiditem.application.command.CreateRaidItemCommand;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.*;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRaidItemAction {
    private final ProjectRepository projects;
    private final RaidItemRepository items;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateRaidItemAction(ProjectRepository projects, RaidItemRepository items,
                                RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.projects = projects;
        this.items = items;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RaidItemResponse execute(CreateRaidItemCommand command) {
        authorization.requireCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw RaidExceptions.projectArchived(command.projectId());
        }
        if (command.title() == null || command.title().isBlank()) {
            throw RaidExceptions.titleRequired();
        }
        RaidItemType type = RaidEnumParser.parseRequired(RaidItemType.class, command.type(), "type");
        RaidItem item = RaidItem.create(project.id(), project.workspaceId(), type, command.code(),
                command.title().trim(), command.description(), command.ownerUserId());
        if (type == RaidItemType.RISK) {
            RaidProbability p = command.probability() == null ? null
                    : RaidEnumParser.parseRequired(RaidProbability.class, command.probability(), "probability");
            RaidImpact i = command.impact() == null ? null
                    : RaidEnumParser.parseRequired(RaidImpact.class, command.impact(), "impact");
            RiskResponseStrategy s = command.riskResponseStrategy() == null ? null
                    : RaidEnumParser.parseRequired(RiskResponseStrategy.class, command.riskResponseStrategy(), "riskResponseStrategy");
            item = item.withRiskFields(p, i, s, command.riskTrigger());
        }
        if (type == RaidItemType.ISSUE) {
            item = item.update(item.title(), item.description(), item.ownerUserId(), command.severity(),
                    command.issueCategory(), command.impactSummary(), command.rootCause(),
                    command.resolutionPlan(), null, null, null);
        }
        if (type == RaidItemType.ASSUMPTION) {
            item = item.update(item.title(), item.description(), item.ownerUserId(), null, null, null, null, null, null,
                    command.assumptionStatement(), command.validationStatus());
        }
        if (type == RaidItemType.DEPENDENCY) {
            item = item.update(item.title(), item.description(), item.ownerUserId(), null, null, null, null, null,
                    command.dependencyType(), null, null);
        }
        item = items.save(item);
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_CREATED, "RAID item created: " + type);
        return RaidItemResponse.from(item);
    }
}
