package com.company.scopery.modules.traceability.dataentityrelation.application.action;

import com.company.scopery.modules.traceability.dataentityrelation.application.command.UpdateRegistryDataEntityRelationCommand;
import com.company.scopery.modules.traceability.dataentityrelation.application.response.RegistryDataEntityRelationResponse;
import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelationRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryDataEntityRelationAction {

    private final RegistryDataEntityRelationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryDataEntityRelationAction(RegistryDataEntityRelationRepository repo,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryDataEntityRelationResponse execute(UpdateRegistryDataEntityRelationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var relation = repo.findByIdAndWorkspaceId(c.relationId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.dataEntityRelationNotFound(c.relationId()));

        var saved = repo.save(relation.withUpdated(
                c.relationType().name(), c.sourceColumn(), c.label(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_RELATION, saved.id(),
                TraceabilityActivityActions.DATA_ENTITY_RELATION_UPDATED,
                "Data entity relation updated");

        return RegistryDataEntityRelationResponse.from(saved);
    }
}
