package com.company.scopery.modules.servicesupport.warranty.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataWarrantyCoverageJpaRepository extends JpaRepository<WarrantyCoverageJpaEntity, UUID> {
    List<WarrantyCoverageJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<WarrantyCoverageJpaEntity> findByProjectId(UUID projectId);
}
