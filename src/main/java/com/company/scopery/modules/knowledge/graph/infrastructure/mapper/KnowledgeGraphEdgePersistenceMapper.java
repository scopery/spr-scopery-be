package com.company.scopery.modules.knowledge.graph.infrastructure.mapper;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphEdgeStatus;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphEdgeType;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphEdge;
import com.company.scopery.modules.knowledge.graph.infrastructure.persistence.KnowledgeGraphEdgeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeGraphEdgePersistenceMapper {

    public KnowledgeGraphEdge toDomain(KnowledgeGraphEdgeJpaEntity entity) {
        return new KnowledgeGraphEdge(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getFromNodeId(),
                entity.getToNodeId(),
                GraphEdgeType.valueOf(entity.getEdgeType()),
                entity.getSourceRefId(),
                GraphEdgeStatus.valueOf(entity.getEdgeStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion() != null ? entity.getVersion() : 0L
        );
    }

    public KnowledgeGraphEdgeJpaEntity toJpaEntity(KnowledgeGraphEdge domain) {
        KnowledgeGraphEdgeJpaEntity entity = new KnowledgeGraphEdgeJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setFromNodeId(domain.fromNodeId());
        entity.setToNodeId(domain.toNodeId());
        entity.setEdgeType(domain.edgeType().name());
        entity.setSourceRefId(domain.sourceRefId());
        entity.setEdgeStatus(domain.edgeStatus().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
