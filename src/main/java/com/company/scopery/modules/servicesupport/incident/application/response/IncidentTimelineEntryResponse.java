package com.company.scopery.modules.servicesupport.incident.application.response;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntry;
import java.time.Instant; import java.util.UUID;
public record IncidentTimelineEntryResponse(UUID id, UUID workspaceId, UUID incidentId, String entryType,
        String visibility, String message, Instant occurredAt) {
    public static IncidentTimelineEntryResponse from(IncidentTimelineEntry d) {
        return new IncidentTimelineEntryResponse(d.id(), d.workspaceId(), d.incidentId(), d.entryType(),
                d.visibility(), d.message(), d.occurredAt());
    }
}
