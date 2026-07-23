package com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.persistence;

import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.mapper.NonFunctionalItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaNonFunctionalItemRepository implements NonFunctionalItemRepository {

    private final SpringDataNonFunctionalItemJpaRepository springData;
    private final NonFunctionalItemPersistenceMapper mapper;

    public JpaNonFunctionalItemRepository(
            SpringDataNonFunctionalItemJpaRepository springData,
            NonFunctionalItemPersistenceMapper mapper
    ) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public NonFunctionalItem save(NonFunctionalItem item) {
        NonFunctionalItemJpaEntity entity = mapper.toJpaEntity(item);
        NonFunctionalItemJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NonFunctionalItem> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId)
                .map(mapper::toDomain);
    }

    @Override
    public List<NonFunctionalItem> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<NonFunctionalItem> findByProjectIdAndIdIn(UUID projectId, Collection<UUID> ids) {
        return springData.findByProjectIdAndIdIn(projectId, ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springData.existsByProjectIdAndCode(projectId, code);
    }

    @Override
    public void delete(UUID id, UUID projectId) {
        springData.deleteByIdAndProjectId(id, projectId);
    }
}
