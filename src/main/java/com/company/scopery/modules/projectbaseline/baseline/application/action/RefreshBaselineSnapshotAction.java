package com.company.scopery.modules.projectbaseline.baseline.application.action;

import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.application.service.BaselineSnapshotService;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RefreshBaselineSnapshotAction {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final BaselineSnapshotService snapshotService;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public RefreshBaselineSnapshotAction(ProjectRepository projects, ProjectBaselineRepository baselines,
                                         BaselineSnapshotService snapshotService,
                                         ProjectBaselineAuthorizationService authorization,
                                         ProjectBaselinePlatformPublisher publisher,
                                         ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.baselines = baselines; this.snapshotService = snapshotService;
        this.authorization = authorization; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectBaselineResponse execute(UUID projectId, UUID baselineId) {
        authorization.requireBaselineUpdate(projectId);
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectBaseline baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
        if (!baseline.isEditable()) throw ProjectBaselineExceptions.baselineNotDraft(baseline.id());
        var snap = snapshotService.build(project, baseline.sourceScheduleRunId(), baseline.sourceEstimationRunId(),
                baseline.sourceFinanceScenarioId(), baseline.sourceQuoteVersionId());
        baseline = baselines.save(baseline.withSnapshot(snap.snapshotJson(), snap.summaryJson()));
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_REFRESHED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_REFRESHED, "PROJECT_BASELINE_REFRESHED");
        return ProjectBaselineResponse.from(baseline);
    }
}
