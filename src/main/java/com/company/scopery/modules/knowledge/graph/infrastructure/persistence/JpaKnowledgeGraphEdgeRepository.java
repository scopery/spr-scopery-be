package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphEdge;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphEdgeRepository;
import com.company.scopery.modules.knowledge.graph.infrastructure.mapper.KnowledgeGraphEdgePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaKnowledgeGraphEdgeRepository implements KnowledgeGraphEdgeRepository {

    private final SpringDataKnowledgeGraphEdgeJpaRepository springData;
    private final KnowledgeGraphEdgePersistenceMapper mapper;

    public JpaKnowledgeGraphEdgeRepository(SpringDataKnowledgeGraphEdgeJpaRepository springData,
                                            KnowledgeGraphEdgePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeGraphEdge save(KnowledgeGraphEdge edge) {
        KnowledgeGraphEdgeJpaEntity entity = mapper.toJpaEntity(edge);
        KnowledgeGraphEdgeJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<KnowledgeGraphEdge> findByFromNodeId(UUID fromNodeId) {
        return springData.findByFromNodeId(fromNodeId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeGraphEdge> findByToNodeId(UUID toNodeId) {
        return springData.findByToNodeId(toNodeId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeGraphEdge> findActiveByNodeId(UUID nodeId) {
        return springData.findActiveByNodeId(nodeId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
