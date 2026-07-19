package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MaintenanceActivityRepository {
    MaintenanceActivity save(MaintenanceActivity activity);
    Optional<MaintenanceActivity> findById(UUID id);
    List<MaintenanceActivity> findByWorkspaceId(UUID workspaceId);
    List<MaintenanceActivity> findByMaintenancePlanId(UUID maintenancePlanId);
}
