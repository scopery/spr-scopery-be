package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.gantt.application.command.RemoveGanttDependencyCommand;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.taskdependency.application.action.RemoveTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.RemoveTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RemoveGanttDependencyAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final RemoveTaskDependencyAction removeTaskDependencyAction;
    private final ProjectRepository projects;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;
    private final RecalculateGanttAction recalculateGanttAction;

    public RemoveGanttDependencyAction(ProjectWorkspaceAuthorizationService authorization,
                                       RemoveTaskDependencyAction removeTaskDependencyAction,
                                       ProjectRepository projects,
                                       ProjectPlatformPublisher publisher,
                                       ProjectActivityLogger activityLogger,
                                       RecalculateGanttAction recalculateGanttAction) {
        this.authorization = authorization;
        this.removeTaskDependencyAction = removeTaskDependencyAction;
        this.projects = projects;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.recalculateGanttAction = recalculateGanttAction;
    }

    @Transactional
    public TaskDependencyResponse execute(RemoveGanttDependencyCommand cmd) {
        authorization.requireGanttEditDependency(cmd.projectId());
        TaskDependencyResponse response = removeTaskDependencyAction.execute(
                new RemoveTaskDependencyCommand(cmd.dependencyId(), cmd.projectId()));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("dependencyId", cmd.dependencyId());
        payload.put("projectId", cmd.projectId());
        publisher.enqueueGantt(cmd.projectId(), "GANTT_DEPENDENCY_REMOVED", payload);
        activityLogger.logSuccess(ProjectEntityTypes.GANTT, cmd.dependencyId(),
                ProjectActivityActions.GANTT_DEPENDENCY_REMOVED, "Gantt dependency removed");

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
