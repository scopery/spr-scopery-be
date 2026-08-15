package com.company.scopery.modules.traceability.componentapi.infrastructure.persistence;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApi;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApiRepository;
import com.company.scopery.modules.traceability.componentapi.infrastructure.mapper.RegistryComponentApiPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryComponentApiRepository implements RegistryComponentApiRepository {

    private final SpringDataRegistryComponentApiJpaRepository springData;
    private final RegistryComponentApiPersistenceMapper mapper;

    public JpaRegistryComponentApiRepository(SpringDataRegistryComponentApiJpaRepository springData,
                                              RegistryComponentApiPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryComponentApi save(RegistryComponentApi entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryComponentApi> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByComponentIdAndApiIdAndRole(UUID componentId, UUID apiId, ComponentApiRole role) {
        return springData.existsByComponentIdAndApiIdAndRole(componentId, apiId, role.name());
    }

    @Override
    public List<RegistryComponentApi> findByComponentId(UUID componentId) {
        return springData.findByComponentIdOrderByDisplayOrderAsc(componentId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id) {
        springData.deleteById(id);
    }
}
