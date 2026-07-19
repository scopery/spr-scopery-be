package com.company.scopery.modules.servicesupport.maintenance.application.response;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivity;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record MaintenanceActivityResponse(UUID id, UUID workspaceId, UUID maintenancePlanId, String activityType,
        String title, BigDecimal effortHours, boolean clientVisible, Instant performedAt, Instant createdAt) {
    public static MaintenanceActivityResponse from(MaintenanceActivity d) {
        return new MaintenanceActivityResponse(d.id(), d.workspaceId(), d.maintenancePlanId(), d.activityType(),
                d.title(), d.effortHours(), d.clientVisible(), d.performedAt(), d.createdAt());
    }
}
