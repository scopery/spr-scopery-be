package com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence;

import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import com.company.scopery.modules.traceability.validationruletype.infrastructure.mapper.RegistryValidationRuleTypePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryValidationRuleTypeRepository implements RegistryValidationRuleTypeRepository {

    private final SpringDataRegistryValidationRuleTypeJpaRepository springData;
    private final RegistryValidationRuleTypePersistenceMapper mapper;

    public JpaRegistryValidationRuleTypeRepository(SpringDataRegistryValidationRuleTypeJpaRepository springData,
                                                    RegistryValidationRuleTypePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryValidationRuleType save(RegistryValidationRuleType entity) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(entity)));
    }

    @Override
    public Optional<RegistryValidationRuleType> findByIdAndAccessible(UUID id, UUID workspaceId) {
        return springData.findByIdAndAccessible(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryValidationRuleType> findAllAccessible(UUID workspaceId) {
        return springData.findAllAccessible(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<RegistryValidationRuleType> findByIdIn(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return springData.findAllById(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCodeAndWorkspaceIdIsNull(String code) {
        return springData.existsByCodeAndWorkspaceIdIsNull(code);
    }

    @Override
    public void delete(UUID id, UUID workspaceId) {
        springData.deleteByIdAndWorkspaceId(id, workspaceId);
    }
}
