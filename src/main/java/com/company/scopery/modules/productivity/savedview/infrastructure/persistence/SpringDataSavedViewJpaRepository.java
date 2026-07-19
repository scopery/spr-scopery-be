package com.company.scopery.modules.productivity.savedview.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataSavedViewJpaRepository extends JpaRepository<SavedViewJpaEntity, UUID> {
    List<SavedViewJpaEntity> findByWorkspaceIdAndOwnerUserIdAndStatus(UUID workspaceId, UUID ownerUserId, String status);
}
