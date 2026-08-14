package com.company.scopery.modules.traceability.screenmode.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenModeRepository {
    RegistryScreenMode save(RegistryScreenMode entity);
    Optional<RegistryScreenMode> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenMode> findByScreenId(UUID screenId);
    List<RegistryScreenMode> findByIdIn(Collection<UUID> ids);
    Optional<RegistryScreenMode> findByScreenIdAndModeCode(UUID screenId, String modeCode);
    void delete(UUID id, UUID workspaceId);
}
