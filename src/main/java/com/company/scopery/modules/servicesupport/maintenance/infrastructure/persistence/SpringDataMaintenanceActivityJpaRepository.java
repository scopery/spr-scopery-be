package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataMaintenanceActivityJpaRepository extends JpaRepository<MaintenanceActivityJpaEntity, UUID> {
    List<MaintenanceActivityJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<MaintenanceActivityJpaEntity> findByMaintenancePlanId(UUID maintenancePlanId);
}
