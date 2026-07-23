package com.company.scopery.modules.traceability.funcitemanchor.infrastructure.persistence;

import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchor;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchorRepository;
import com.company.scopery.modules.traceability.funcitemanchor.infrastructure.mapper.FunctionalItemAnchorPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionalItemAnchorRepository implements FunctionalItemAnchorRepository {

    private final SpringDataFunctionalItemAnchorJpaRepository springData;
    private final FunctionalItemAnchorPersistenceMapper mapper;

    public JpaFunctionalItemAnchorRepository(
            SpringDataFunctionalItemAnchorJpaRepository springData,
            FunctionalItemAnchorPersistenceMapper mapper
    ) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionalItemAnchor save(FunctionalItemAnchor anchor) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(anchor)));
    }

    @Override
    public Optional<FunctionalItemAnchor> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId) {
        return springData.findByIdAndFunctionalItemId(id, functionalItemId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionalItemAnchor> findByFunctionalItemId(UUID functionalItemId) {
        return springData.findByFunctionalItemIdOrderByCreatedAtAsc(functionalItemId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByFunctionalItemIdAndNodeTypeAndNodeId(UUID functionalItemId, String nodeType, UUID nodeId) {
        return springData.existsByFunctionalItemIdAndNodeTypeAndNodeId(functionalItemId, nodeType, nodeId);
    }

    @Override
    public List<FunctionalItemAnchor> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<FunctionalItemAnchor> findByNodeTypeAndNodeIdAndProjectId(String nodeType, UUID nodeId, UUID projectId) {
        return springData.findByNodeTypeAndNodeIdAndProjectId(nodeType, nodeId, projectId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id, UUID functionalItemId) {
        springData.deleteByIdAndFunctionalItemId(id, functionalItemId);
    }
}
