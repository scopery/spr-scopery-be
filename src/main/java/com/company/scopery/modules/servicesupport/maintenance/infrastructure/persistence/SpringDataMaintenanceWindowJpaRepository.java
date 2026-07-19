package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataMaintenanceWindowJpaRepository extends JpaRepository<MaintenanceWindowJpaEntity, UUID> {
    List<MaintenanceWindowJpaEntity> findByWorkspaceId(UUID workspaceId);
}
