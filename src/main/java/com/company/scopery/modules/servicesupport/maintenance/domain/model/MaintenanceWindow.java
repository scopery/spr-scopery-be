package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.time.Instant; import java.util.UUID;
public record MaintenanceWindow(UUID id, UUID workspaceId, UUID maintenancePlanId, String title,
        Instant windowStart, Instant windowEnd, String status, int version, Instant createdAt, Instant updatedAt) {
    public static MaintenanceWindow schedule(UUID workspaceId, UUID maintenancePlanId, String title, Instant start, Instant end) {
        if (end != null && start != null && end.isBefore(start)) throw new IllegalArgumentException("end before start");
        Instant now = Instant.now();
        return new MaintenanceWindow(UUID.randomUUID(), workspaceId, maintenancePlanId, title, start, end, "SCHEDULED", 0, now, now);
    }
    public MaintenanceWindow complete() {
        if (!"SCHEDULED".equals(status) && !"IN_PROGRESS".equals(status)) throw new IllegalStateException("invalid");
        return new MaintenanceWindow(id, workspaceId, maintenancePlanId, title, windowStart, windowEnd, "COMPLETED", version, createdAt, Instant.now());
    }
}
