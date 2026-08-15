package com.company.scopery.modules.traceability.componentfield.application.action;

import com.company.scopery.modules.traceability.componentfield.application.command.CreateRegistryComponentFieldCommand;
import com.company.scopery.modules.traceability.componentfield.application.response.RegistryComponentFieldResponse;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryComponentFieldAction {

    private final RegistryComponentFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryComponentFieldAction(RegistryComponentFieldRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentFieldResponse execute(CreateRegistryComponentFieldCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        if (repo.existsByComponentIdAndFieldKey(c.componentId(), c.fieldKey())) {
            throw TraceabilityExceptions.componentFieldKeyExists(c.fieldKey());
        }

        var saved = repo.save(RegistryComponentField.create(
                c.componentId(), c.workspaceId(), c.fieldKey(), c.label(), c.fieldType(),
                c.required(), c.maxLength(), c.remark(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_FIELD, saved.id(),
                TraceabilityActivityActions.COMPONENT_FIELD_CREATED,
                "Component field created: " + saved.fieldKey());
        return RegistryComponentFieldResponse.from(saved);
    }
}
