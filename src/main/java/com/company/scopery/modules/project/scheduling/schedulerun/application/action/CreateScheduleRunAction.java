package com.company.scopery.modules.project.scheduling.schedulerun.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduling.engine.ScheduleEngineService;
import com.company.scopery.modules.project.scheduling.schedulerun.application.command.CreateScheduleRunCommand;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class CreateScheduleRunAction {

    private final ProjectRepository projects;
    private final ScheduleRunRepository runs;
    private final TaskScheduleRepository taskSchedules;
    private final ProjectWorkspaceAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ScheduleEngineService engine;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;

    public CreateScheduleRunAction(ProjectRepository projects,
                                   ScheduleRunRepository runs,
                                   TaskScheduleRepository taskSchedules,
                                   ProjectWorkspaceAuthorizationService authorization,
                                   CurrentUserAuthorizationService currentUser,
                                   ScheduleEngineService engine,
                                   ProjectPlatformPublisher publisher,
                                   ProjectActivityLogger activityLogger) {
        this.projects = projects;
        this.runs = runs;
        this.taskSchedules = taskSchedules;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.engine = engine;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScheduleRunResponse execute(CreateScheduleRunCommand command) {
        authorization.requireScheduleRecalculate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.scheduleRunProjectArchived(project.id());
        }
        if (command.planningStartDate() == null
                || command.planningEndDate() == null
                || command.planningEndDate().isBefore(command.planningStartDate())) {
            throw ProjectExceptions.scheduleRunInvalidDateRange();
        }
        if (ChronoUnit.DAYS.between(command.planningStartDate(), command.planningEndDate()) + 1
                > ScheduleEngineService.MAX_PLANNING_DAYS) {
            throw ProjectExceptions.scheduleRunRangeTooLarge();
        }

        UUID actorId = currentUser.resolveCurrentUser().id();
        ScheduleRun run = ScheduleRun.create(
                project.id(),
                project.workspaceId(),
                command.planningStartDate(),
                command.planningEndDate(),
                "{\"includeCompletedTasks\":" + command.includeCompletedTasks()
                        + ",\"markAsCurrent\":" + command.markAsCurrent() + "}",
                actorId,
                MDC.get("traceId"));
        run = runs.save(run);
        publisher.enqueueScheduleRun(run, "SCHEDULE_RUN_CREATED");
        publisher.enqueueScheduleRun(run, "SCHEDULE_RUN_STARTED");
        activityLogger.logSuccess(ProjectEntityTypes.SCHEDULE_RUN, run.id(),
                ProjectActivityActions.SCHEDULE_RUN_CREATED, "Schedule run created");

        ScheduleRun result = engine.execute(run, command.includeCompletedTasks(), command.markAsCurrent());

        boolean completed = result.status() == ScheduleRunStatus.COMPLETED;
        publisher.enqueueScheduleRun(result, completed ? "SCHEDULE_RUN_COMPLETED" : "SCHEDULE_RUN_FAILED");
        publisher.auditScheduleRecalculation(actorId, project, result);
        if (command.markAsCurrent() && completed) {
            publisher.auditScheduleMarkedCurrent(actorId, project, result);
        }
        activityLogger.logSuccess(ProjectEntityTypes.SCHEDULE_RUN, result.id(),
                completed ? ProjectActivityActions.SCHEDULE_RUN_COMPLETED : ProjectActivityActions.SCHEDULE_RUN_FAILED,
                "Schedule run " + result.status().name().toLowerCase());

        if (completed) {
            logTaskScheduleActivities(result.id());
        }
        return ScheduleRunResponse.from(result);
    }

    private void logTaskScheduleActivities(UUID scheduleRunId) {
        for (TaskSchedule schedule : taskSchedules.findAllByScheduleRunId(scheduleRunId)) {
            if (schedule.riskStatus() == TaskScheduleRiskStatus.AT_RISK
                    || schedule.riskStatus() == TaskScheduleRiskStatus.OVERDUE) {
                activityLogger.logSuccess(ProjectEntityTypes.TASK_SCHEDULE, schedule.id(),
                        ProjectActivityActions.TASK_SCHEDULE_AT_RISK,
                        "Task schedule at risk: " + schedule.taskId());
                publisher.enqueueTaskSchedule(schedule, "TASK_SCHEDULE_AT_RISK");
            }
            if (schedule.scheduleStatus() == TaskScheduleStatus.UNSCHEDULED
                    || schedule.scheduleStatus() == TaskScheduleStatus.PARTIALLY_SCHEDULED) {
                activityLogger.logSuccess(ProjectEntityTypes.TASK_SCHEDULE, schedule.id(),
                        ProjectActivityActions.TASK_SCHEDULE_UNSCHEDULED,
                        "Task schedule unscheduled: " + schedule.taskId());
                publisher.enqueueTaskSchedule(schedule, "TASK_SCHEDULE_UNSCHEDULED");
            }
        }
    }
}
