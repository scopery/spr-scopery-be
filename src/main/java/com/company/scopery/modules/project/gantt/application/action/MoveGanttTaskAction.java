package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.gantt.application.command.MoveGanttTaskCommand;
import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.scheduleoverride.application.response.TaskScheduleOverrideResponse;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
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

@Component
public class MoveGanttTaskAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final TaskRepository tasks;
    private final TaskScheduleOverrideRepository overrides;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUser;
    private final RecalculateGanttAction recalculateGanttAction;

    public MoveGanttTaskAction(ProjectWorkspaceAuthorizationService authorization,
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
    public TaskScheduleOverrideResponse execute(MoveGanttTaskCommand cmd) {
        authorization.requireGanttMoveTask(cmd.projectId());
        Project project = mutationGuard.requireMutableProject(cmd.projectId());

        if (cmd.reason() == null || cmd.reason().isBlank()) {
            throw ProjectExceptions.ganttTaskOverrideReasonRequired();
        }
        if (cmd.manualStartDate() == null && cmd.manualFinishDate() == null) {
            throw ProjectExceptions.ganttTaskOverrideInvalidDateRange();
        }
        if (cmd.manualStartDate() != null && cmd.manualFinishDate() != null
                && cmd.manualFinishDate().isBefore(cmd.manualStartDate())) {
            throw ProjectExceptions.ganttTaskOverrideInvalidDateRange();
        }

        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.ganttTaskNotFound(cmd.taskId()));
        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.ganttTaskPathMismatch(cmd.taskId(), cmd.projectId());
        }

        var actorId = currentUser.resolveCurrentUser().id();
        overrides.findActiveByTaskId(cmd.taskId()).ifPresent(active ->
                overrides.save(active.cancel(actorId)));

        ScheduleOverrideType type = cmd.manualStartDate() != null && cmd.manualFinishDate() != null
                ? ScheduleOverrideType.PIN_RANGE
                : (cmd.manualStartDate() != null ? ScheduleOverrideType.PIN_START : ScheduleOverrideType.PIN_FINISH);

        // Does NOT change task.estimateHours — move only pins dates.
        TaskScheduleOverride saved = overrides.save(TaskScheduleOverride.create(
                cmd.projectId(), cmd.taskId(), type,
                cmd.manualStartDate(), cmd.manualFinishDate(), null, cmd.reason().trim()));

        publisher.enqueueOverride(saved, "GANTT_TASK_MOVED");
        publisher.auditGanttTaskMoved(actorId, project, saved);
        activityLogger.logSuccess(ProjectEntityTypes.TASK_SCHEDULE_OVERRIDE, saved.id(),
                ProjectActivityActions.GANTT_TASK_MOVED, "Gantt task moved: " + cmd.taskId());

        if (cmd.recalculate()) {
            LocalDateRange range = defaultPlanningRange(project);
            recalculateGanttAction.execute(new RecalculateGanttCommand(
                    cmd.projectId(), range.start(), range.end(), false, true));
        }
        return TaskScheduleOverrideResponse.from(saved);
    }

    private LocalDateRange defaultPlanningRange(Project project) {
        var start = project.plannedStartDate() != null ? project.plannedStartDate() : java.time.LocalDate.now();
        var end = project.plannedEndDate() != null ? project.plannedEndDate() : start.plusDays(90);
        return new LocalDateRange(start, end);
    }

    private record LocalDateRange(java.time.LocalDate start, java.time.LocalDate end) {}
}
