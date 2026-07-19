package com.company.scopery.modules.productivity.savedview.domain.model;
import java.util.*; import java.util.UUID;
public interface SavedViewRepository {
    SavedView save(SavedView v);
    Optional<SavedView> findById(UUID id);
    List<SavedView> findActiveByWorkspaceAndOwner(UUID workspaceId, UUID ownerUserId);
}
