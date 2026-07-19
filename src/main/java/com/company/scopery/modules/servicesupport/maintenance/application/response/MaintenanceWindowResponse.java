package com.company.scopery.modules.servicesupport.maintenance.application.response;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindow;
import java.time.Instant; import java.util.UUID;
public record MaintenanceWindowResponse(UUID id, UUID workspaceId, UUID maintenancePlanId, String title,
        Instant windowStart, Instant windowEnd, String status, Instant createdAt) {
    public static MaintenanceWindowResponse from(MaintenanceWindow d) {
        return new MaintenanceWindowResponse(d.id(), d.workspaceId(), d.maintenancePlanId(), d.title(),
                d.windowStart(), d.windowEnd(), d.status(), d.createdAt());
    }
}
