package com.company.scopery.modules.trust.sensitivefield.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensitiveFieldRegistryRepository {
    SensitiveFieldRegistry save(SensitiveFieldRegistry entry);
    Optional<SensitiveFieldRegistry> findById(UUID id);
    List<SensitiveFieldRegistry> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndObjectTypeCodeAndFieldPath(UUID workspaceId, String objectTypeCode, String fieldPath);
}
