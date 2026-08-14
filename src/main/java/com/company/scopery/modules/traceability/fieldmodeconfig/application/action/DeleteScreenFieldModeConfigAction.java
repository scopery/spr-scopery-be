package com.company.scopery.modules.traceability.fieldmodeconfig.application.action;

import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.DeleteScreenFieldModeConfigCommand;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DeleteScreenFieldModeConfigAction {

    private final RegistryScreenFieldModeConfigRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteScreenFieldModeConfigAction(RegistryScreenFieldModeConfigRepository repo,
                                              TraceabilityAuthorizationService authorization,
                                              TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteScreenFieldModeConfigCommand command) {
        authorization.requireWorkspaceCreate(command.workspaceId());

        repo.findByIdAndWorkspaceId(command.configId(), command.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.fieldModeConfigNotFound(command.configId()));

        repo.deleteByIdIn(List.of(command.configId()));

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_FIELD_MODE_CONFIG, command.configId(),
                TraceabilityActivityActions.FIELD_MODE_CONFIGS_REPLACED,
                "Field mode config deleted: " + command.configId());
    }
}
