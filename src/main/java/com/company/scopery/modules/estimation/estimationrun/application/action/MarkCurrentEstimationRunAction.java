package com.company.scopery.modules.estimation.estimationrun.application.action;

import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.domain.enums.EstimationRunStatus;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.shared.activity.EstimationActivityLogger;
import com.company.scopery.modules.estimation.shared.authorization.EstimationAuthorizationService;
import com.company.scopery.modules.estimation.shared.constant.EstimationActivityActions;
import com.company.scopery.modules.estimation.shared.constant.EstimationEntityTypes;
import com.company.scopery.modules.estimation.shared.error.EstimationExceptions;
import com.company.scopery.modules.estimation.shared.support.EstimationPlatformPublisher;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkCurrentEstimationRunAction {

    private final EstimationRunRepository runs;
    private final ProjectRepository projects;
    private final EstimationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final EstimationPlatformPublisher publisher;
    private final EstimationActivityLogger activityLogger;

    public MarkCurrentEstimationRunAction(EstimationRunRepository runs,
                                          ProjectRepository projects,
                                          EstimationAuthorizationService authorization,
                                          CurrentUserAuthorizationService currentUser,
                                          EstimationPlatformPublisher publisher,
                                          EstimationActivityLogger activityLogger) {
        this.runs = runs;
        this.projects = projects;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EstimationRunResponse execute(UUID projectId, UUID runId) {
        authorization.requireMarkCurrent(projectId);
        EstimationRun run = runs.findById(runId)
                .filter(r -> r.projectId().equals(projectId))
                .orElseThrow(() -> EstimationExceptions.runNotFound(runId));
        if (run.status() != EstimationRunStatus.COMPLETED) {
            throw EstimationExceptions.runNotCompleted(runId);
        }
        Project project = projects.findById(projectId)
                .orElseThrow(() -> EstimationExceptions.runNotFound(runId));
        projects.save(project.withCurrentEstimationRunId(run.id()));
        publisher.enqueueRun(run, "ESTIMATION_RUN_MARKED_CURRENT");
        publisher.auditMarkedCurrent(currentUser.resolveCurrentUser().id(), project, run);
        activityLogger.logSuccess(EstimationEntityTypes.ESTIMATION_RUN, run.id(),
                EstimationActivityActions.ESTIMATION_RUN_MARKED_CURRENT, "Estimation run marked current");
        return EstimationRunResponse.from(run);
    }
}
