package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNode;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNodeRepository;
import com.company.scopery.modules.knowledge.graph.infrastructure.mapper.KnowledgeGraphNodePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeGraphNodeRepository implements KnowledgeGraphNodeRepository {

    private final SpringDataKnowledgeGraphNodeJpaRepository springData;
    private final KnowledgeGraphNodePersistenceMapper mapper;

    public JpaKnowledgeGraphNodeRepository(SpringDataKnowledgeGraphNodeJpaRepository springData,
                                            KnowledgeGraphNodePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeGraphNode save(KnowledgeGraphNode node) {
        KnowledgeGraphNodeJpaEntity entity = mapper.toJpaEntity(node);
        KnowledgeGraphNodeJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeGraphNode> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeGraphNode> findByRef(UUID workspaceId, GraphNodeType nodeType,
                                                   UUID sourceRefId, UUID sourceVersionRefId) {
        return springData.findByWorkspaceIdAndNodeTypeAndSourceRefIdAndSourceVersionRefId(
                workspaceId, nodeType.name(), sourceRefId, sourceVersionRefId
        ).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeGraphNode> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
