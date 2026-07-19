package com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataResourceProfileJpaRepository extends JpaRepository<ResourceProfileJpaEntity, UUID> {
    List<ResourceProfileJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId);
    Optional<ResourceProfileJpaEntity> findByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId);
}
