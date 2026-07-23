package com.company.scopery.modules.traceability.structurerelation.infrastructure.persistence;

import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelation;
import com.company.scopery.modules.traceability.structurerelation.domain.model.StructureRelationRepository;
import com.company.scopery.modules.traceability.structurerelation.infrastructure.mapper.StructureRelationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaStructureRelationRepository implements StructureRelationRepository {

    private final SpringDataStructureRelationJpaRepository springData;
    private final StructureRelationPersistenceMapper mapper;

    public JpaStructureRelationRepository(SpringDataStructureRelationJpaRepository springData,
                                          StructureRelationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public StructureRelation save(StructureRelation rel) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(rel)));
    }

    @Override
    public Optional<StructureRelation> findByIdAndApplicationId(UUID id, UUID applicationId) {
        return springData.findByIdAndApplicationId(id, applicationId).map(mapper::toDomain);
    }

    @Override
    public List<StructureRelation> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtAsc(applicationId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<StructureRelation> findByApplicationIdAndFromNode(UUID applicationId, String nodeType, UUID nodeId) {
        return springData.findByApplicationIdAndFromNodeTypeAndFromNodeIdOrderByCreatedAtAsc(applicationId, nodeType, nodeId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<StructureRelation> findByApplicationIdAndToNode(UUID applicationId, String nodeType, UUID nodeId) {
        return springData.findByApplicationIdAndToNodeTypeAndToNodeIdOrderByCreatedAtAsc(applicationId, nodeType, nodeId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByApplicationIdAndNodes(UUID applicationId, String fromNodeType, UUID fromNodeId,
                                                 String toNodeType, UUID toNodeId) {
        return springData.existsByApplicationIdAndFromNodeTypeAndFromNodeIdAndToNodeTypeAndToNodeId(
                applicationId, fromNodeType, fromNodeId, toNodeType, toNodeId);
    }

    @Override
    public void delete(UUID id, UUID applicationId) {
        springData.deleteByIdAndApplicationId(id, applicationId);
    }
}
