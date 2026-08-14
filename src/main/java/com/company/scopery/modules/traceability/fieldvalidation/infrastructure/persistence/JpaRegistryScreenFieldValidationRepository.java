package com.company.scopery.modules.traceability.fieldvalidation.infrastructure.persistence;

import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.fieldvalidation.infrastructure.mapper.RegistryScreenFieldValidationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenFieldValidationRepository implements RegistryScreenFieldValidationRepository {

    private final SpringDataRegistryScreenFieldValidationJpaRepository springData;
    private final RegistryScreenFieldValidationPersistenceMapper mapper;

    public JpaRegistryScreenFieldValidationRepository(
            SpringDataRegistryScreenFieldValidationJpaRepository springData,
            RegistryScreenFieldValidationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryScreenFieldValidation save(RegistryScreenFieldValidation entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryScreenFieldValidation> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryScreenFieldValidation> findByFieldId(UUID fieldId) {
        return springData.findByFieldId(fieldId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryScreenFieldValidation> findByFieldIdIn(Collection<UUID> fieldIds) {
        return springData.findByFieldIdIn(fieldIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
