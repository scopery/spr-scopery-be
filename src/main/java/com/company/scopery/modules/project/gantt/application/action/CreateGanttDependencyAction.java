package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.project.gantt.application.command.CreateGanttDependencyCommand;
import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CreateGanttDependencyAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final CreateTaskDependencyAction createTaskDependencyAction;
    private final ProjectRepository projects;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;
    private final RecalculateGanttAction recalculateGanttAction;

    public CreateGanttDependencyAction(ProjectWorkspaceAuthorizationService authorization,
                                       CreateTaskDependencyAction createTaskDependencyAction,
                                       ProjectRepository projects,
                                       ProjectPlatformPublisher publisher,
                                       ProjectActivityLogger activityLogger,
                                       RecalculateGanttAction recalculateGanttAction) {
        this.authorization = authorization;
        this.createTaskDependencyAction = createTaskDependencyAction;
        this.projects = projects;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.recalculateGanttAction = recalculateGanttAction;
    }

    @Transactional
    public TaskDependencyResponse execute(CreateGanttDependencyCommand cmd) {
        authorization.requireGanttEditDependency(cmd.projectId());
        // Reuses TaskDependency validation (self/cycle/duplicate).
        TaskDependencyResponse response = createTaskDependencyAction.execute(new CreateTaskDependencyCommand(
                cmd.projectId(),
                cmd.predecessorTaskId(),
                cmd.successorTaskId(),
                cmd.dependencyType(),
                cmd.lagDays()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("dependencyId", response.id());
        payload.put("projectId", cmd.projectId());
        payload.put("predecessorTaskId", cmd.predecessorTaskId());
        payload.put("successorTaskId", cmd.successorTaskId());
        publisher.enqueueGantt(cmd.projectId(), "GANTT_DEPENDENCY_CREATED", payload);
        activityLogger.logSuccess(ProjectEntityTypes.GANTT, response.id(),
                ProjectActivityActions.GANTT_DEPENDENCY_CREATED, "Gantt dependency created");

        if (cmd.recalculate()) {
            Project project = projects.findById(cmd.projectId())
                    .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
            LocalDate start = project.plannedStartDate() != null ? project.plannedStartDate() : LocalDate.now();
            LocalDate end = project.plannedEndDate() != null ? project.plannedEndDate() : start.plusDays(90);
            recalculateGanttAction.execute(new RecalculateGanttCommand(
                    cmd.projectId(), start, end, false, true));
        }
        return response;
    }
}
