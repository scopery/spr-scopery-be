package com.company.scopery.modules.productivity.workinbox.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataWorkInboxItemJpaRepository extends JpaRepository<WorkInboxItemJpaEntity, UUID> {
    Optional<WorkInboxItemJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<WorkInboxItemJpaEntity> findByWorkspaceIdAndUserIdAndStatusInOrderByDueAtAsc(UUID workspaceId, UUID userId, List<String> statuses);
}
