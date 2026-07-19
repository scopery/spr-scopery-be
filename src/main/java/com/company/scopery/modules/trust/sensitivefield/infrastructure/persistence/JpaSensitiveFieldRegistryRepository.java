package com.company.scopery.modules.trust.sensitivefield.infrastructure.persistence;

import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import com.company.scopery.modules.trust.sensitivefield.infrastructure.mapper.SensitiveFieldRegistryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSensitiveFieldRegistryRepository implements SensitiveFieldRegistryRepository {
    private final SpringDataSensitiveFieldRegistryJpaRepository spring;
    private final SensitiveFieldRegistryPersistenceMapper mapper;

    public JpaSensitiveFieldRegistryRepository(
            SpringDataSensitiveFieldRegistryJpaRepository spring,
            SensitiveFieldRegistryPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public SensitiveFieldRegistry save(SensitiveFieldRegistry entry) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(entry)));
    }

    @Override
    public Optional<SensitiveFieldRegistry> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SensitiveFieldRegistry> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByWorkspaceIdAndObjectTypeCodeAndFieldPath(
            UUID workspaceId, String objectTypeCode, String fieldPath) {
        return spring.findByWorkspaceIdAndObjectTypeCodeAndFieldPath(workspaceId, objectTypeCode, fieldPath).isPresent();
    }
}
