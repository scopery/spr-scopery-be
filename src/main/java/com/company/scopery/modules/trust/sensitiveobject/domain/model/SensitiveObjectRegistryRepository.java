package com.company.scopery.modules.trust.sensitiveobject.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensitiveObjectRegistryRepository {
    SensitiveObjectRegistry save(SensitiveObjectRegistry entry);
    Optional<SensitiveObjectRegistry> findById(UUID id);
    List<SensitiveObjectRegistry> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndObjectTypeCode(UUID workspaceId, String objectTypeCode);
}
