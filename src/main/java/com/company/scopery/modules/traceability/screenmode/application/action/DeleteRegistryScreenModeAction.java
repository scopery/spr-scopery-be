package com.company.scopery.modules.traceability.screenmode.application.action;

import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryScreenModeAction {

    private final RegistryScreenModeRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryScreenModeAction(RegistryScreenModeRepository repo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID modeId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(modeId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenModeNotFound(modeId));

        repo.delete(modeId, workspaceId);

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_MODE, modeId,
                TraceabilityActivityActions.SCREEN_MODE_DELETED, "Screen mode deleted: " + modeId);
    }
}
