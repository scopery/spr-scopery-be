package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record MaintenanceActivity(UUID id, UUID workspaceId, UUID maintenanceWindowId, UUID maintenancePlanId,
        UUID serviceProfileId, UUID projectId, String activityType, String title, String description,
        String outcomeSummary, BigDecimal effortHours, boolean clientVisible,
        Instant performedAt, UUID performedBy, int version, Instant createdAt, Instant updatedAt) {
    public static MaintenanceActivity create(UUID workspaceId, UUID maintenancePlanId, String activityType, String title) {
        Instant now = Instant.now();
        return new MaintenanceActivity(UUID.randomUUID(), workspaceId, null, maintenancePlanId, null, null,
                activityType, title, null, null, null, false, now, null, 0, now, now);
    }
}
