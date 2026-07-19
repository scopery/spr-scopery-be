package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MaintenancePlanRepository {
    MaintenancePlan save(MaintenancePlan p); Optional<MaintenancePlan> findById(UUID id); List<MaintenancePlan> findByWorkspaceId(UUID workspaceId);
}
