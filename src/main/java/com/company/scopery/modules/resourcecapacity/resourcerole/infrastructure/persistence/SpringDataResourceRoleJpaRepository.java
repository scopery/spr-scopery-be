package com.company.scopery.modules.resourcecapacity.resourcerole.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataResourceRoleJpaRepository extends JpaRepository<ResourceRoleJpaEntity, UUID> {
    List<ResourceRoleJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndRoleCode(UUID workspaceId, String code);
}
