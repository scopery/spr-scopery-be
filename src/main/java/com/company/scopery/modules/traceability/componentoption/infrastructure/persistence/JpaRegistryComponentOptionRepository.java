package com.company.scopery.modules.traceability.componentoption.infrastructure.persistence;

import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.componentoption.infrastructure.mapper.RegistryComponentOptionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryComponentOptionRepository implements RegistryComponentOptionRepository {

    private final SpringDataRegistryComponentOptionJpaRepository springData;
    private final RegistryComponentOptionPersistenceMapper mapper;

    public JpaRegistryComponentOptionRepository(SpringDataRegistryComponentOptionJpaRepository springData,
                                                 RegistryComponentOptionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryComponentOption save(RegistryComponentOption entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryComponentOption> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryComponentOption> findByComponentId(UUID componentId) {
        return springData.findByComponentIdOrderByDisplayOrderAsc(componentId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryComponentOption> findByComponentIdIn(Collection<UUID> componentIds) {
        if (componentIds == null || componentIds.isEmpty()) return List.of();
        return springData.findByComponentIdInOrderByDisplayOrderAsc(componentIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
