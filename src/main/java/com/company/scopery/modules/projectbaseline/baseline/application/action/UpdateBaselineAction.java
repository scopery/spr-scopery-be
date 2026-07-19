package com.company.scopery.modules.projectbaseline.baseline.application.action;

import com.company.scopery.modules.projectbaseline.baseline.application.command.UpdateBaselineCommand;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaselineRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateBaselineAction {
    private final ProjectBaselineRepository baselines;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public UpdateBaselineAction(ProjectBaselineRepository baselines,
                                ProjectBaselineAuthorizationService authorization,
                                ProjectBaselinePlatformPublisher publisher,
                                ProjectBaselineActivityLogger activityLogger) {
        this.baselines = baselines; this.authorization = authorization;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectBaselineResponse execute(UpdateBaselineCommand cmd) {
        authorization.requireBaselineUpdate(cmd.projectId());
        ProjectBaseline baseline = baselines.findByIdAndProjectId(cmd.baselineId(), cmd.projectId())
                .orElseThrow(() -> ProjectBaselineExceptions.baselineNotFound(cmd.baselineId()));
        if (!baseline.isEditable()) throw ProjectBaselineExceptions.baselineNotDraft(baseline.id());
        baseline = baselines.save(baseline.updateDraft(cmd.name(), cmd.description()));
        publisher.enqueueBaseline(baseline, ProjectBaselineEventCodes.PROJECT_BASELINE_CREATED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.PROJECT_BASELINE, baseline.id(),
                ProjectBaselineActivityActions.PROJECT_BASELINE_UPDATED, "PROJECT_BASELINE_UPDATED");
        return ProjectBaselineResponse.from(baseline);
    }
}
