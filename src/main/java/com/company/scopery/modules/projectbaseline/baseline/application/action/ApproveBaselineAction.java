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
public class ApproveBaselineAction {
    private final ProjectRepository projects;
    private final ProjectBaselineRepository baselines;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;
    private final ValidateBaselineAction validateBaselineAction;

    public ApproveBaselineAction(ProjectRepository projects, ProjectBaselineRepository baselines,
                                 ProjectBaselineAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser,
                                 ProjectBaselinePlatformPublisher publisher,
                                 ProjectBaselineActivityLogger activityLogger,
                                 ValidateBaselineAction validateBaselineAction) {
        this.projects = projects; this.baselines = baselines; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher; this.activityLogger = activityLogger;
        this.validateBaselineAction = validateBaselineAction;
    }

    @Transactional
    public ProjectBaselineResponse execute(UUID projectId, UUID baselineId) {
        authorization.requireBaselineApprove(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ProjectBaseline baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
        if (baseline.status() == BaselineStatus.APPROVED) {
            throw ProjectBaselineExceptions.baselineImmutable(baseline.id());
        }
        if (baseline.status() == BaselineStatus.DRAFT) {
            validateBaselineAction.execute(projectId, baselineId);
            baseline = baselines.findByIdAndProjectId(baselineId, projectId)
                    .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(baselineId));
        }
        if (baseline.status() != BaselineStatus.READY && baseline.status() != BaselineStatus.APPROVED) {
            throw ProjectBaselineExceptions.baselineInvalidStatus(baseline.id(), "approve");
        }
        baseline = baselines.save(baseline.approve(actorId));
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_APPROVED);
        publisher.audit(AuditEventType.PROJECT_BASELINE_APPROVED, actorId, project,
                ProjectBaselinePlatformPublisher.AGG_BASELINE, baseline.id(), "ApproveBaselineAction");
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_APPROVED, "PROJECT_BASELINE_APPROVED");
        return ProjectBaselineResponse.from(baseline);
    }
}
