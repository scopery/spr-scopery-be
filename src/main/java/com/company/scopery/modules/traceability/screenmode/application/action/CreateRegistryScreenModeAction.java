package com.company.scopery.modules.traceability.screenmode.application.action;

import com.company.scopery.modules.traceability.screenmode.application.command.CreateRegistryScreenModeCommand;
import com.company.scopery.modules.traceability.screenmode.application.response.RegistryScreenModeResponse;
import com.company.scopery.modules.traceability.screenmode.domain.enums.RegistryScreenModeCode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryScreenModeAction {

    private final RegistryScreenModeRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryScreenModeAction(RegistryScreenModeRepository repo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryScreenModeResponse execute(CreateRegistryScreenModeCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        RegistryScreenModeCode modeCode = TraceabilityEnumParser.parseRequired(
                RegistryScreenModeCode.class, c.modeCode(), "modeCode");

        if (repo.findByScreenIdAndModeCode(c.screenId(), modeCode.name()).isPresent()) {
            throw TraceabilityExceptions.screenModeCodeExists(modeCode.name());
        }

        RegistryScreenMode saved = repo.save(RegistryScreenMode.create(
                c.screenId(), c.workspaceId(), modeCode.name(), c.name().trim(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_MODE, saved.id(),
                TraceabilityActivityActions.SCREEN_MODE_CREATED, "Screen mode created: " + modeCode.name());

        return RegistryScreenModeResponse.from(saved);
    }
}
