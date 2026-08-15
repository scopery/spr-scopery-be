package com.company.scopery.modules.traceability.dataentityrelation.application.action;

import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelationRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryDataEntityRelationAction {

    private final RegistryDataEntityRelationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryDataEntityRelationAction(RegistryDataEntityRelationRepository repo,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID relationId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(relationId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityRelationNotFound(relationId));

        repo.deleteById(relationId);

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_RELATION, relationId,
                TraceabilityActivityActions.DATA_ENTITY_RELATION_DELETED,
                "Data entity relation deleted");
    }
}
