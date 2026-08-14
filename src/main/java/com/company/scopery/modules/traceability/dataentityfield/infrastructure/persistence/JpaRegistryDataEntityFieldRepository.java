package com.company.scopery.modules.traceability.dataentityfield.infrastructure.persistence;

import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.dataentityfield.infrastructure.mapper.RegistryDataEntityFieldPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryDataEntityFieldRepository implements RegistryDataEntityFieldRepository {

    private final SpringDataRegistryDataEntityFieldJpaRepository springData;
    private final RegistryDataEntityFieldPersistenceMapper mapper;

    public JpaRegistryDataEntityFieldRepository(SpringDataRegistryDataEntityFieldJpaRepository springData,
                                                 RegistryDataEntityFieldPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryDataEntityField save(RegistryDataEntityField entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryDataEntityField> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryDataEntityField> findByEntityId(UUID entityId) {
        return springData.findByEntityIdOrderByDisplayOrderAsc(entityId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryDataEntityField> findByIdIn(Collection<UUID> ids) {
        return springData.findByIdIn(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
