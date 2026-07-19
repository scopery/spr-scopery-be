package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataPermissionReviewFindingJpaRepository extends JpaRepository<PermissionReviewFindingJpaEntity, UUID> {
    List<PermissionReviewFindingJpaEntity> findByWorkspaceId(UUID workspaceId);
}
