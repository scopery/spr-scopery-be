package com.company.scopery.modules.traceability.validationruletype.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryValidationRuleTypeRepository {
    RegistryValidationRuleType save(RegistryValidationRuleType entity);
    Optional<RegistryValidationRuleType> findByIdAndAccessible(UUID id, UUID workspaceId);
    List<RegistryValidationRuleType> findAllAccessible(UUID workspaceId);
    List<RegistryValidationRuleType> findByIdIn(Collection<UUID> ids);
    boolean existsByCodeAndWorkspaceIdIsNull(String code);
    void delete(UUID id, UUID workspaceId);
}
