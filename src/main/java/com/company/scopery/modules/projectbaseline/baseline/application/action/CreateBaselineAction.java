package com.company.scopery.modules.projectbaseline.baseline.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.baseline.application.command.CreateBaselineCommand;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.application.service.BaselineSnapshotService;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateBaselineAction {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final BaselineSnapshotService snapshotService;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public CreateBaselineAction(ProjectRepository projects, ProjectBaselineRepository baselines,
                                BaselineSnapshotService snapshotService,
                                ProjectBaselineAuthorizationService authorization,
                                CurrentUserAuthorizationService currentUser,
                                ProjectBaselinePlatformPublisher publisher,
                                ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.baselines = baselines; this.snapshotService = snapshotService;
        this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectBaselineResponse execute(CreateBaselineCommand cmd) {
        authorization.requireBaselineCreate(cmd.projectId());
        Project project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectBaselineExceptions.projectArchived(project.id());
        }
        UUID scheduleId = cmd.sourceScheduleRunId() != null ? cmd.sourceScheduleRunId() : project.currentScheduleRunId();
        UUID estimationId = cmd.sourceEstimationRunId() != null ? cmd.sourceEstimationRunId() : project.currentEstimationRunId();
        UUID financeId = cmd.sourceFinanceScenarioId() != null ? cmd.sourceFinanceScenarioId() : project.currentFinanceScenarioId();
        UUID quoteVersionId = cmd.sourceQuoteVersionId() != null ? cmd.sourceQuoteVersionId() : project.currentQuoteVersionId();

        var snap = snapshotService.build(project, scheduleId, estimationId, financeId, quoteVersionId);
        int number = baselines.nextBaselineNumber(project.id());
        ProjectBaseline baseline = ProjectBaseline.create(
                project.id(), project.workspaceId(), number, cmd.name(), cmd.description(),
                scheduleId, estimationId, financeId, quoteVersionId,
                snap.snapshotJson(), snap.summaryJson(), MDC.get("traceId"));
        baseline = baselines.save(baseline);
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_CREATED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_CREATED, "PROJECT_BASELINE_CREATED");
        return ProjectBaselineResponse.from(baseline);
    }
}
