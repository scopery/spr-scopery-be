package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.UUID;
public interface SpringDataMaintenancePlanJpaRepository extends JpaRepository<MaintenancePlanJpaEntity, UUID> { List<MaintenancePlanJpaEntity> findByWorkspaceId(UUID workspaceId); }
