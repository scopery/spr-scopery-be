package com.company.scopery.modules.productivity.pin.domain.model;
import java.util.*; import java.util.UUID;
public interface PinnedItemRepository {
    PinnedItem save(PinnedItem p);
    Optional<PinnedItem> findById(UUID id);
    List<PinnedItem> findActiveByWorkspaceAndOwner(UUID workspaceId, UUID ownerUserId);
}
