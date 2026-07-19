package com.company.scopery.modules.estimation.estimationrun.application.action;

import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRunRepository;
import com.company.scopery.modules.estimation.shared.activity.EstimationActivityLogger;
import com.company.scopery.modules.estimation.shared.authorization.EstimationAuthorizationService;
import com.company.scopery.modules.estimation.shared.constant.EstimationActivityActions;
import com.company.scopery.modules.estimation.shared.constant.EstimationEntityTypes;
import com.company.scopery.modules.estimation.shared.error.EstimationExceptions;
import com.company.scopery.modules.estimation.shared.support.EstimationPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CancelEstimationRunAction {

    private final EstimationRunRepository runs;
    private final EstimationAuthorizationService authorization;
    private final EstimationPlatformPublisher publisher;
    private final EstimationActivityLogger activityLogger;

    public CancelEstimationRunAction(EstimationRunRepository runs,
                                     EstimationAuthorizationService authorization,
                                     EstimationPlatformPublisher publisher,
                                     EstimationActivityLogger activityLogger) {
        this.runs = runs;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EstimationRunResponse execute(UUID projectId, UUID runId) {
        authorization.requireCancel(projectId);
        EstimationRun run = runs.findById(runId)
                .filter(r -> r.projectId().equals(projectId))
                .orElseThrow(() -> EstimationExceptions.runNotFound(runId));
        if (!run.cancellable()) {
            throw EstimationExceptions.notCancellable(runId);
        }
        EstimationRun cancelled = runs.save(run.cancelled());
        publisher.enqueueRun(cancelled, "ESTIMATION_RUN_CANCELLED");
        activityLogger.logSuccess(EstimationEntityTypes.ESTIMATION_RUN, cancelled.id(),
                EstimationActivityActions.ESTIMATION_RUN_CANCELLED, "Estimation run cancelled");
        return EstimationRunResponse.from(cancelled);
    }
}
