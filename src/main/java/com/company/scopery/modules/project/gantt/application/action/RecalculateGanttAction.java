package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.modules.project.gantt.application.command.RecalculateGanttCommand;
import com.company.scopery.modules.project.gantt.application.query.GanttViewQuery;
import com.company.scopery.modules.project.gantt.application.response.GanttRecalculateResponse;
import com.company.scopery.modules.project.gantt.application.service.GanttQueryService;
import com.company.scopery.modules.project.scheduling.schedulerun.application.action.CreateScheduleRunAction;
import com.company.scopery.modules.project.scheduling.schedulerun.application.command.CreateScheduleRunCommand;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RecalculateGanttAction {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final CreateScheduleRunAction createScheduleRunAction;
    private final GanttQueryService ganttQueryService;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;

    public RecalculateGanttAction(ProjectWorkspaceAuthorizationService authorization,
                                  CreateScheduleRunAction createScheduleRunAction,
                                  GanttQueryService ganttQueryService,
                                  ProjectPlatformPublisher publisher,
                                  ProjectActivityLogger activityLogger) {
        this.authorization = authorization;
        this.createScheduleRunAction = createScheduleRunAction;
        this.ganttQueryService = ganttQueryService;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public GanttRecalculateResponse execute(RecalculateGanttCommand cmd) {
        authorization.requireGanttRecalculate(cmd.projectId());

        Map<String, Object> requested = new LinkedHashMap<>();
        requested.put("projectId", cmd.projectId());
        requested.put("planningStartDate", cmd.planningStartDate() != null ? cmd.planningStartDate().toString() : null);
        requested.put("planningEndDate", cmd.planningEndDate() != null ? cmd.planningEndDate().toString() : null);
        publisher.enqueueGantt(cmd.projectId(), "GANTT_RECALCULATION_REQUESTED", requested);

        ScheduleRunResponse run = createScheduleRunAction.execute(new CreateScheduleRunCommand(
                cmd.projectId(),
                cmd.planningStartDate(),
                cmd.planningEndDate(),
                cmd.includeCompletedTasks(),
                cmd.markAsCurrent()));

        Map<String, Object> recalculated = new LinkedHashMap<>(requested);
        recalculated.put("scheduleRunId", run.id());
        recalculated.put("status", run.status());
        publisher.enqueueGantt(cmd.projectId(), "GANTT_RECALCULATED", recalculated);
        activityLogger.logSuccess(ProjectEntityTypes.GANTT, run.id(),
                ProjectActivityActions.GANTT_RECALCULATED, "Gantt recalculated via schedule run " + run.id());

        var view = ganttQueryService.getView(new GanttViewQuery(
                cmd.projectId(), run.id(), null, null, true, false, "PHASE"));
        return new GanttRecalculateResponse(run, view.summary());
    }
}
