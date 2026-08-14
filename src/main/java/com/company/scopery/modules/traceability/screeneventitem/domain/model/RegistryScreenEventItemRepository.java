package com.company.scopery.modules.traceability.screeneventitem.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenEventItemRepository {
    RegistryScreenEventItem save(RegistryScreenEventItem item);
    Optional<RegistryScreenEventItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenEventItem> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status);
    List<RegistryScreenEventItem> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
