package com.company.scopery.modules.knowledge.graph.domain.model;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeGraphNodeRepository {
    KnowledgeGraphNode save(KnowledgeGraphNode node);
    Optional<KnowledgeGraphNode> findById(UUID id);
    Optional<KnowledgeGraphNode> findByRef(UUID workspaceId, GraphNodeType nodeType, UUID sourceRefId, UUID sourceVersionRefId);
    List<KnowledgeGraphNode> findByWorkspaceId(UUID workspaceId);
}
