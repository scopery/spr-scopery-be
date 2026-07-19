package com.company.scopery.modules.servicesupport.incident.domain.model;
import java.time.Instant; import java.util.UUID;
public record IncidentTimelineEntry(UUID id, UUID workspaceId, UUID incidentId, String entryType, String visibility,
        String message, Instant occurredAt, int version, Instant createdAt, Instant updatedAt) {
    public static IncidentTimelineEntry create(UUID workspaceId, UUID incidentId, String entryType, String visibility, String message) {
        Instant now = Instant.now();
        return new IncidentTimelineEntry(UUID.randomUUID(), workspaceId, incidentId, entryType, visibility, message,
                now, 0, now, now);
    }
}
