package com.company.scopery.modules.servicesupport.maintenance.application.response;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlan;
import java.time.Instant; import java.util.UUID;
public record MaintenancePlanResponse(UUID id, UUID workspaceId, UUID projectId, String name, String status,
        Instant plannedStart, Instant plannedEnd, Instant createdAt) {
    public static MaintenancePlanResponse from(MaintenancePlan d) {
        return new MaintenancePlanResponse(d.id(), d.workspaceId(), d.projectId(), d.name(), d.status(),
                d.plannedStart(), d.plannedEnd(), d.createdAt());
    }
}
