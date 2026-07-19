package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MaintenanceWindowRepository {
    MaintenanceWindow save(MaintenanceWindow window);
    Optional<MaintenanceWindow> findById(UUID id);
    List<MaintenanceWindow> findByWorkspaceId(UUID workspaceId);
}
