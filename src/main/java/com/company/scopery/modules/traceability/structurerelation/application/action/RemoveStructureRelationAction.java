package com.company.scopery.modules.traceability.structurerelation.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveStructureRelationAction {

    private final StructureRelationRepository repo;
    private final TraceabilityActivityLogger activityLogger;

    public RemoveStructureRelationAction(StructureRelationRepository repo,
                                         TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID id, UUID applicationId) {
        repo.findByIdAndApplicationId(id, applicationId)
                .orElseThrow(() -> TraceabilityExceptions.structureRelationNotFound(id));
        repo.delete(id, applicationId);
        activityLogger.logSuccess(TraceabilityEntityTypes.STRUCTURE_RELATION, id,
                TraceabilityActivityActions.STRUCTURE_RELATION_REMOVED, "Structure relation removed");
    }
}
