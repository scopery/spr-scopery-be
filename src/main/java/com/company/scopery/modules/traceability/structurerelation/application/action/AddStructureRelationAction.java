package com.company.scopery.modules.traceability.structurerelation.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.structurerelation.application.command.AddStructureRelationCommand;
import com.company.scopery.modules.traceability.structurerelation.application.response.StructureRelationResponse;
import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationNodeType;
import com.company.scopery.modules.traceability.structurerelation.domain.enums.StructureRelationType;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelation;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddStructureRelationAction {

    private final StructureRelationRepository repo;
    private final TraceabilityActivityLogger activityLogger;

    public AddStructureRelationAction(StructureRelationRepository repo,
                                      TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public StructureRelationResponse execute(AddStructureRelationCommand c) {
        StructureRelationNodeType fromType = TraceabilityEnumParser.parseRequired(
                StructureRelationNodeType.class, c.fromNodeType(), "fromNodeType");
        StructureRelationNodeType toType = TraceabilityEnumParser.parseRequired(
                StructureRelationNodeType.class, c.toNodeType(), "toNodeType");
        StructureRelationType relationType = c.relationType() != null
                ? TraceabilityEnumParser.parseRequired(StructureRelationType.class, c.relationType(), "relationType")
                : StructureRelationType.RELATED;

        if (fromType == toType && c.fromNodeId().equals(c.toNodeId())) {
            throw TraceabilityExceptions.structureRelationSelfLoop();
        }
        if (repo.existsByApplicationIdAndNodes(c.applicationId(), fromType.name(), c.fromNodeId(),
                toType.name(), c.toNodeId())) {
            throw TraceabilityExceptions.structureRelationDuplicate();
        }

        StructureRelation saved = repo.save(StructureRelation.create(
                c.applicationId(), c.workspaceId(),
                fromType, c.fromNodeId(),
                toType, c.toNodeId(),
                relationType));

        activityLogger.logSuccess(TraceabilityEntityTypes.STRUCTURE_RELATION, saved.id(),
                TraceabilityActivityActions.STRUCTURE_RELATION_ADDED, "Structure relation added");

        return StructureRelationResponse.from(saved);
    }
}
