package com.company.scopery.modules.servicesupport.incident.application.response;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecord;
import java.time.Instant; import java.util.UUID;
public record SupportIncidentResponse(UUID id, UUID workspaceId, UUID projectId, String incidentNumber, String title,
        String severity, String status, Instant detectedAt, Instant acknowledgedAt, Instant resolvedAt, Instant closedAt) {
    public static SupportIncidentResponse from(SupportIncidentRecord d) {
        return new SupportIncidentResponse(d.id(), d.workspaceId(), d.projectId(), d.incidentNumber(), d.title(),
                d.severity(), d.status(), d.detectedAt(), d.acknowledgedAt(), d.resolvedAt(), d.closedAt());
    }
}
