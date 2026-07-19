package com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshot;
import com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.persistence.AiPlanningContextSnapshotJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningContextSnapshotPersistenceMapper {
    public AiPlanningContextSnapshot toDomain(AiPlanningContextSnapshotJpaEntity e) {
        return new AiPlanningContextSnapshot(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getActorUserId(), e.getContextType(),
                e.getAccessScopeJson(), e.getIncludedSectionsJson(), e.getRedactionSummaryJson(),
                e.getContextPayloadJson(), e.getTokenEstimate(), e.getTraceId(), e.getCreatedAt());
    }

    public AiPlanningContextSnapshotJpaEntity toJpaEntity(AiPlanningContextSnapshot d) {
        AiPlanningContextSnapshotJpaEntity e = new AiPlanningContextSnapshotJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setActorUserId(d.actorUserId());
        e.setContextType(d.contextType());
        e.setAccessScopeJson(d.accessScopeJson());
        e.setIncludedSectionsJson(d.includedSectionsJson());
        e.setRedactionSummaryJson(d.redactionSummaryJson());
        e.setContextPayloadJson(d.contextPayloadJson());
        e.setTokenEstimate(d.tokenEstimate());
        e.setTraceId(d.traceId());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
