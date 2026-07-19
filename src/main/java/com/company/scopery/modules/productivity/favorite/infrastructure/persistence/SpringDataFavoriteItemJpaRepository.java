package com.company.scopery.modules.productivity.favorite.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataFavoriteItemJpaRepository extends JpaRepository<FavoriteItemJpaEntity, UUID> {
    Optional<FavoriteItemJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<FavoriteItemJpaEntity> findByWorkspaceIdAndUserIdAndArchivedAtIsNullOrderByCreatedAtDesc(UUID workspaceId, UUID userId);
}
