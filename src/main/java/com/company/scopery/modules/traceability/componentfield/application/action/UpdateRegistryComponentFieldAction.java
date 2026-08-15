package com.company.scopery.modules.traceability.componentfield.application.action;

import com.company.scopery.modules.traceability.componentfield.application.command.UpdateRegistryComponentFieldCommand;
import com.company.scopery.modules.traceability.componentfield.application.response.RegistryComponentFieldResponse;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryComponentFieldAction {

    private final RegistryComponentFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryComponentFieldAction(RegistryComponentFieldRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentFieldResponse execute(UpdateRegistryComponentFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var field = repo.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.componentFieldNotFound(c.fieldId()));
        var saved = repo.save(field.withUpdated(
                c.label(), c.fieldType(), c.required(), c.maxLength(), c.remark(), c.displayOrder()));
        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_FIELD, saved.id(),
                TraceabilityActivityActions.COMPONENT_FIELD_UPDATED,
                "Component field updated: " + saved.fieldKey());
        return RegistryComponentFieldResponse.from(saved);
    }
}
