package com.company.scopery.modules.trust.sensitiveobject.infrastructure.persistence;

import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistry;
import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistryRepository;
import com.company.scopery.modules.trust.sensitiveobject.infrastructure.mapper.SensitiveObjectRegistryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSensitiveObjectRegistryRepository implements SensitiveObjectRegistryRepository {
    private final SpringDataSensitiveObjectRegistryJpaRepository spring;
    private final SensitiveObjectRegistryPersistenceMapper mapper;

    public JpaSensitiveObjectRegistryRepository(
            SpringDataSensitiveObjectRegistryJpaRepository spring,
            SensitiveObjectRegistryPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public SensitiveObjectRegistry save(SensitiveObjectRegistry entry) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(entry)));
    }

    @Override
    public Optional<SensitiveObjectRegistry> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SensitiveObjectRegistry> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByWorkspaceIdAndObjectTypeCode(UUID workspaceId, String objectTypeCode) {
        return spring.findByWorkspaceIdAndObjectTypeCode(workspaceId, objectTypeCode).isPresent();
    }
}
