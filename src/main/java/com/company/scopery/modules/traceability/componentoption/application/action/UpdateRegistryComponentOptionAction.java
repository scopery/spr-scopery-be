package com.company.scopery.modules.traceability.componentoption.application.action;

import com.company.scopery.modules.traceability.componentoption.application.command.UpdateRegistryComponentOptionCommand;
import com.company.scopery.modules.traceability.componentoption.application.response.RegistryComponentOptionResponse;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryComponentOptionAction {

    private final RegistryComponentOptionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryComponentOptionAction(RegistryComponentOptionRepository repo,
                                                TraceabilityAuthorizationService authorization,
                                                TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentOptionResponse execute(UpdateRegistryComponentOptionCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryComponentOption existing = repo.findByIdAndWorkspaceId(c.optionId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.componentOptionNotFound(c.optionId()));
        RegistryComponentOption updated = existing.withUpdated(c.optionValue(), c.optionLabel(), c.displayOrder());
        RegistryComponentOption saved = repo.save(updated);
        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_OPTION, saved.id(),
                TraceabilityActivityActions.COMPONENT_OPTION_UPDATED,
                "Component option updated: " + saved.optionValue());
        return RegistryComponentOptionResponse.from(saved);
    }
}
