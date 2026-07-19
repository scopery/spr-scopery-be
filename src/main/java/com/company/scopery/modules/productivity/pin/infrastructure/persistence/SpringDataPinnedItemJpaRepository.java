package com.company.scopery.modules.productivity.pin.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataPinnedItemJpaRepository extends JpaRepository<PinnedItemJpaEntity, UUID> {
    List<PinnedItemJpaEntity> findByWorkspaceIdAndOwnerUserIdAndArchivedAtIsNullOrderBySortOrderAsc(UUID workspaceId, UUID ownerUserId);
}
