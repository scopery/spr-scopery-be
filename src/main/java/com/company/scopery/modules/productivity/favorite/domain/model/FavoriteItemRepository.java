package com.company.scopery.modules.productivity.favorite.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface FavoriteItemRepository {
    FavoriteItem save(FavoriteItem item);
    Optional<FavoriteItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<FavoriteItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId);
}
