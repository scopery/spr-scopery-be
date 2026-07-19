package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataTagAssignmentJpaRepository extends JpaRepository<TagAssignmentJpaEntity, UUID> {
    List<TagAssignmentJpaEntity> findByWorkspaceIdAndArchivedAtIsNull(UUID workspaceId);
}
