package com.company.scopery.modules.traceability.fieldvalidation.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenFieldValidationRepository {
    RegistryScreenFieldValidation save(RegistryScreenFieldValidation entity);
    Optional<RegistryScreenFieldValidation> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenFieldValidation> findByFieldId(UUID fieldId);
    List<RegistryScreenFieldValidation> findByFieldIdIn(Collection<UUID> fieldIds);
    void delete(UUID id, UUID workspaceId);
}
