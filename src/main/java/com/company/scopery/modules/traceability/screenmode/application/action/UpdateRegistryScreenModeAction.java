package com.company.scopery.modules.traceability.screenmode.application.action;

import com.company.scopery.modules.traceability.screenmode.application.command.UpdateRegistryScreenModeCommand;
import com.company.scopery.modules.traceability.screenmode.application.response.RegistryScreenModeResponse;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryScreenModeAction {

    private final RegistryScreenModeRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryScreenModeAction(RegistryScreenModeRepository repo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenModeResponse execute(UpdateRegistryScreenModeCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var mode = repo.findByIdAndWorkspaceId(c.modeId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.screenModeNotFound(c.modeId()));

        var saved = repo.save(mode.withUpdated(c.name().trim(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_MODE, saved.id(),
                TraceabilityActivityActions.SCREEN_MODE_UPDATED, "Screen mode updated: " + saved.modeCode());

        return RegistryScreenModeResponse.from(saved);
    }
}
