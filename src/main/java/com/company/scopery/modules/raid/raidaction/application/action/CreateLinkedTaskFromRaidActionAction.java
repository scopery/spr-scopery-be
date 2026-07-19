package com.company.scopery.modules.raid.raidaction.application.action;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.application.action.CreateTaskAction;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.raid.raidaction.application.command.CreateLinkedTaskFromRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.CreateLinkedTaskFromRaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.UUID;

@Component
public class CreateLinkedTaskFromRaidActionAction {
    private static final BigDecimal DEFAULT_ESTIMATE_HOURS = new BigDecimal("1.0");

    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final RaidItemRepository items;
    private final RaidActionRepository actions;
    private final CreateTaskAction createTaskAction;
    private final RaidAuthorizationService authorization;
    private final RaidActivityLogger activityLogger;

    public CreateLinkedTaskFromRaidActionAction(ProjectRepository projects, ProjectPhaseRepository phases,
                                                RaidItemRepository items, RaidActionRepository actions,
                                                CreateTaskAction createTaskAction,
                                                RaidAuthorizationService authorization,
                                                RaidActivityLogger activityLogger) {
        this.projects = projects;
        this.phases = phases;
        this.items = items;
        this.actions = actions;
        this.createTaskAction = createTaskAction;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public CreateLinkedTaskFromRaidActionResponse execute(CreateLinkedTaskFromRaidActionCommand command) {
        authorization.requireUpdate(command.projectId());
        projects.findById(command.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        var action = actions.findByIdAndProjectId(command.raidActionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.actionNotFound(command.raidActionId()));
        var raidItemId = action.raidItemId();
        items.findByIdAndProjectId(raidItemId, command.projectId())
                .orElseThrow(() -> RaidExceptions.itemNotFound(raidItemId));
        if (action.linkedTaskId() != null) {
            throw RaidExceptions.actionAlreadyLinked(command.raidActionId());
        }

        UUID phaseId = resolvePhaseId(command.projectId(), command.phaseId());
        BigDecimal estimateHours = command.estimateHours() != null ? command.estimateHours() : DEFAULT_ESTIMATE_HOURS;
        String taskCode = "RAID-TASK-" + action.id().toString().substring(0, 8).toUpperCase();

        var task = createTaskAction.execute(new CreateTaskCommand(
                command.projectId(),
                phaseId,
                command.wbsNodeId(),
                taskCode,
                action.title(),
                action.description(),
                action.ownerUserId(),
                null,
                null,
                estimateHours,
                null,
                action.dueDate(),
                null
        ));

        action = actions.save(action.withLinkedTask(task.id()));
        activityLogger.logSuccess(RaidEntityTypes.ACTION, action.id(), RaidActivityActions.ACTION_LINKED_TASK,
                "Linked task created: " + task.id());
        return CreateLinkedTaskFromRaidActionResponse.from(action);
    }

    private UUID resolvePhaseId(UUID projectId, UUID requestedPhaseId) {
        if (requestedPhaseId != null) {
            return requestedPhaseId;
        }
        return phases.findAllByProjectId(projectId).stream()
                .filter(p -> p.status() == ProjectPhaseStatus.ACTIVE || p.status() == ProjectPhaseStatus.PLANNED)
                .min(Comparator.comparingInt(ProjectPhase::displayOrder))
                .map(ProjectPhase::id)
                .orElseThrow(() -> RaidExceptions.noProjectPhase(projectId));
    }
}
