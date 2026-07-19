package com.company.scopery.modules.productivity.savedsearch.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataSavedSearchJpaRepository extends JpaRepository<SavedSearchJpaEntity, UUID> {
    Optional<SavedSearchJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<SavedSearchJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
