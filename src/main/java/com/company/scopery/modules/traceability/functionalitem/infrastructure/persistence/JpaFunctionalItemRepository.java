package com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.functionalitem.infrastructure.mapper.FunctionalItemPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFunctionalItemRepository implements FunctionalItemRepository {

    private final SpringDataFunctionalItemJpaRepository springData;
    private final FunctionalItemPersistenceMapper mapper;

    public JpaFunctionalItemRepository(SpringDataFunctionalItemJpaRepository springData,
                                       FunctionalItemPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public FunctionalItem save(FunctionalItem item) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item)));
    }

    @Override
    public Optional<FunctionalItem> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<FunctionalItem> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<FunctionalItem> findByProjectIdAndModuleId(UUID projectId, UUID moduleId) {
        return springData.findByProjectIdAndModuleIdOrderByCreatedAtDesc(projectId, moduleId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<FunctionalItem> findByModuleIdIn(Collection<UUID> moduleIds) {
        return springData.findByModuleIdIn(moduleIds).stream().map(mapper::toDomain).toList();
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
