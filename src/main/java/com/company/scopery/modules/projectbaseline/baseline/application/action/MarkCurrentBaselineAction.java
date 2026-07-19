package com.company.scopery.modules.projectbaseline.baseline.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.domain.enums.BaselineStatus;
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
public class MarkCurrentBaselineAction {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public MarkCurrentBaselineAction(ProjectRepository projects, ProjectBaselineRepository baselines,
                                     ProjectBaselineAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     ProjectBaselinePlatformPublisher publisher,
                                     ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.baselines = baselines; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectBaselineResponse execute(UUID projectId, UUID baselineId) {
        authorization.requireBaselineMarkCurrent(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectBaseline baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
        if (baseline.status() != BaselineStatus.APPROVED) {
            throw ProjectBaselineExceptions.baselineNotApproved(baseline.id());
        }
        for (ProjectBaseline current : baselines.findCurrentFlagged(projectId)) {
            if (!current.id().equals(baseline.id())) {
                baselines.save(current.withCurrentFlag(false));
            }
        }
        baseline = baselines.save(baseline.withCurrentFlag(true));
        projects.save(project.withCurrentBaselineId(baseline.id()));
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_MARKED_CURRENT);
        publisher.audit(AuditEventType.PROJECT_BASELINE_MARKED_CURRENT, actorId, project,
                ProjectBaselinePlatformPublisher.AGG_BASELINE, baseline.id(), "MarkCurrentBaselineAction");
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_MARKED_CURRENT, "PROJECT_BASELINE_MARKED_CURRENT");
        return ProjectBaselineResponse.from(baseline);
    }
}
