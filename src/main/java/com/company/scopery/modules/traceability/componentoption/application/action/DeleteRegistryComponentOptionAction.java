package com.company.scopery.modules.traceability.componentoption.application.action;

import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryComponentOptionAction {

    private final RegistryComponentOptionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryComponentOptionAction(RegistryComponentOptionRepository repo,
                                                TraceabilityAuthorizationService authorization,
                                                TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID optionId) {
        authorization.requireWorkspaceCreate(workspaceId);
        RegistryComponentOption existing = repo.findByIdAndWorkspaceId(optionId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.componentOptionNotFound(optionId));
        repo.delete(optionId, workspaceId);
        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_OPTION, optionId,
                TraceabilityActivityActions.COMPONENT_OPTION_DELETED,
                "Component option deleted: " + existing.optionValue());
    }
}
