package com.company.scopery.modules.traceability.screenprocessitem.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenProcessItemRepository {
    RegistryScreenProcessItem save(RegistryScreenProcessItem item);
    Optional<RegistryScreenProcessItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenProcessItem> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status);
    List<RegistryScreenProcessItem> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
