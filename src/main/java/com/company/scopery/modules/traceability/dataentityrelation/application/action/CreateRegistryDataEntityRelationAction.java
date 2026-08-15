package com.company.scopery.modules.traceability.dataentityrelation.application.action;

import com.company.scopery.modules.traceability.dataentityrelation.application.command.CreateRegistryDataEntityRelationCommand;
import com.company.scopery.modules.traceability.dataentityrelation.application.response.RegistryDataEntityRelationResponse;
import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelation;
import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelationRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryDataEntityRelationAction {

    private final RegistryDataEntityRelationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryDataEntityRelationAction(RegistryDataEntityRelationRepository repo,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryDataEntityRelationResponse execute(CreateRegistryDataEntityRelationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        if (repo.existsBySourceEntityIdAndTargetEntityIdAndRelationType(
                c.sourceEntityId(), c.targetEntityId(), c.relationType().name())) {
            throw TraceabilityExceptions.dataEntityRelationDuplicate(
                    c.sourceEntityId(), c.targetEntityId(), c.relationType().name());
        }

        RegistryDataEntityRelation saved = repo.save(RegistryDataEntityRelation.create(
                c.sourceEntityId(), c.targetEntityId(), c.workspaceId(),
                c.relationType().name(), c.sourceColumn(), c.label(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_RELATION, saved.id(),
                TraceabilityActivityActions.DATA_ENTITY_RELATION_CREATED,
                "Data entity relation created: " + saved.relationType());

        return RegistryDataEntityRelationResponse.from(saved);
    }
}
