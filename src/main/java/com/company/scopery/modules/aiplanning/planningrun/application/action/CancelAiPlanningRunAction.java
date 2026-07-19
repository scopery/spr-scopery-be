package com.company.scopery.modules.aiplanning.planningrun.application.action;

import com.company.scopery.modules.aiplanning.planningrun.application.command.CancelAiPlanningRunCommand;
import com.company.scopery.modules.aiplanning.planningrun.application.response.AiPlanningRunResponse;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRunRepository;
import com.company.scopery.modules.aiplanning.shared.activity.AiPlanningActivityLogger;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningActivityActions;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningEntityTypes;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import com.company.scopery.modules.aiplanning.shared.support.AiPlanningPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelAiPlanningRunAction {
    private final AiPlanningRunRepository runs;
    private final AiPlanningAuthorizationService authorization;
    private final AiPlanningPlatformPublisher publisher;
    private final AiPlanningActivityLogger activityLogger;

    public CancelAiPlanningRunAction(AiPlanningRunRepository runs,
                                     AiPlanningAuthorizationService authorization,
                                     AiPlanningPlatformPublisher publisher,
                                     AiPlanningActivityLogger activityLogger) {
        this.runs = runs; this.authorization = authorization;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public AiPlanningRunResponse execute(CancelAiPlanningRunCommand command) {
        authorization.requireRun(command.projectId());
        AiPlanningRun run = runs.findByIdAndProjectId(command.runId(), command.projectId())
                .orElseThrow(() -> AiPlanningExceptions.runNotFound(command.runId()));
        try {
            run = runs.save(run.cancel());
        } catch (IllegalStateException ex) {
            throw AiPlanningExceptions.runNotCancellable(command.runId());
        }
        publisher.enqueueRun(run, "AI_PLANNING_RUN_CANCELLED");
        activityLogger.logSuccess(AiPlanningEntityTypes.PLANNING_RUN, run.id(),
                AiPlanningActivityActions.RUN_CANCELLED, "AI planning run cancelled");
        return AiPlanningRunResponse.from(run);
    }
}
