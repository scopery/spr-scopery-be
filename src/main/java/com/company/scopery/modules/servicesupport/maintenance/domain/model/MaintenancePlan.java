package com.company.scopery.modules.servicesupport.maintenance.domain.model;
import java.time.Instant; import java.util.UUID;
public record MaintenancePlan(UUID id, UUID workspaceId, UUID projectId, String name, String status,
        Instant plannedStart, Instant plannedEnd, int version, Instant createdAt) {
    public static MaintenancePlan create(UUID workspaceId, UUID projectId, String name, Instant start, Instant end) {
        return new MaintenancePlan(UUID.randomUUID(), workspaceId, projectId, name, "PLANNED", start, end, 0, Instant.now());
    }
}
