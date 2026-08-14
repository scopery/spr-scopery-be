package com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.persistence;

import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.mapper.RegistryScreenFieldModeConfigPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenFieldModeConfigRepository implements RegistryScreenFieldModeConfigRepository {

    private final SpringDataRegistryScreenFieldModeConfigJpaRepository springData;
    private final RegistryScreenFieldModeConfigPersistenceMapper mapper;

    public JpaRegistryScreenFieldModeConfigRepository(
            SpringDataRegistryScreenFieldModeConfigJpaRepository springData,
            RegistryScreenFieldModeConfigPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryScreenFieldModeConfig save(RegistryScreenFieldModeConfig entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public List<RegistryScreenFieldModeConfig> saveAll(List<RegistryScreenFieldModeConfig> entities) {
        List<RegistryScreenFieldModeConfigJpaEntity> jpaEntities = entities.stream()
                .map(mapper::toJpaEntity)
                .toList();
        return springData.saveAllAndFlush(jpaEntities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<RegistryScreenFieldModeConfig> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryScreenFieldModeConfig> findByFieldId(UUID fieldId) {
        return springData.findByFieldId(fieldId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryScreenFieldModeConfig> findByFieldIdIn(Collection<UUID> fieldIds) {
        return springData.findByFieldIdIn(fieldIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int deleteByIdIn(List<UUID> ids) {
        return springData.deleteByIdIn(ids);
    }
}
