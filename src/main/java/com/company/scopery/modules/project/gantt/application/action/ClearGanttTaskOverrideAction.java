package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.gantt.application.command.ClearGanttTaskOverrideCommand;
import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.scheduleoverride.application.response.TaskScheduleOverrideResponse;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class ClearGanttTaskOverrideAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final TaskRepository tasks;
    private final TaskScheduleOverrideRepository overrides;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUser;
    private final RecalculateGanttAction recalculateGanttAction;

    public ClearGanttTaskOverrideAction(ProjectWorkspaceAuthorizationService authorization,
                                        ProjectMutationGuard mutationGuard,
                                        TaskRepository tasks,
                                        TaskScheduleOverrideRepository overrides,
                                        ProjectPlatformPublisher publisher,
                                        ProjectActivityLogger activityLogger,
                                        CurrentUserAuthorizationService currentUser,
                                        RecalculateGanttAction recalculateGanttAction) {
        this.authorization = authorization;
        this.mutationGuard = mutationGuard;
        this.tasks = tasks;
        this.overrides = overrides;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.currentUser = currentUser;
        this.recalculateGanttAction = recalculateGanttAction;
    }

    @Transactional
    public TaskScheduleOverrideResponse execute(ClearGanttTaskOverrideCommand cmd) {
        authorization.requireGanttClearOverride(cmd.projectId());
        Project project = mutationGuard.requireMutableProject(cmd.projectId());

        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.ganttTaskNotFound(cmd.taskId()));
        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.ganttTaskPathMismatch(cmd.taskId(), cmd.projectId());
        }

        TaskScheduleOverride active = overrides.findActiveByTaskId(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.ganttTaskOverrideNotFound(cmd.taskId()));

        var actorId = currentUser.resolveCurrentUser().id();
        TaskScheduleOverride saved = overrides.save(active.cancel(actorId));

        publisher.enqueueOverride(saved, "GANTT_TASK_OVERRIDE_CLEARED");
        publisher.auditGanttOverrideCleared(actorId, project, saved);
        activityLogger.logSuccess(ProjectEntityTypes.TASK_SCHEDULE_OVERRIDE, saved.id(),
                ProjectActivityActions.GANTT_TASK_OVERRIDE_CLEARED,
                "Gantt task override cleared: " + cmd.taskId());

        if (cmd.recalculate()) {
            LocalDate start = project.plannedStartDate() != null ? project.plannedStartDate() : LocalDate.now();
            LocalDate end = project.plannedEndDate() != null ? project.plannedEndDate() : start.plusDays(90);
            recalculateGanttAction.execute(new RecalculateGanttCommand(
                    cmd.projectId(), start, end, false, true));
        }
        return TaskScheduleOverrideResponse.from(saved);
    }
}
