package com.company.scopery.modules.servicesupport.incident.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportIncidentRecord(UUID id, UUID workspaceId, UUID serviceProfileId, UUID projectId,
        String incidentNumber, String title, String description, String severity, String status,
        String impactSummary, String clientVisibleSummary, UUID ownerUserId,
        Instant detectedAt, Instant acknowledgedAt, Instant resolvedAt, Instant closedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportIncidentRecord create(UUID workspaceId, UUID projectId, String title, String severity) {
        Instant now = Instant.now();
        String number = "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new SupportIncidentRecord(UUID.randomUUID(), workspaceId, null, projectId, number, title, null,
                severity, "OPEN", null, null, null, now, null, null, null, 0, now, now);
    }
    public SupportIncidentRecord acknowledge() {
        if (!"OPEN".equals(status)) throw new IllegalStateException("Only OPEN can be acknowledged");
        Instant now = Instant.now();
        return new SupportIncidentRecord(id, workspaceId, serviceProfileId, projectId, incidentNumber, title, description,
                severity, "ACKNOWLEDGED", impactSummary, clientVisibleSummary, ownerUserId,
                detectedAt, now, resolvedAt, closedAt, version, createdAt, now);
    }
    public SupportIncidentRecord resolve(String newImpactSummary) {
        if ("CLOSED".equals(status) || "RESOLVED".equals(status)) throw new IllegalStateException("Cannot resolve terminal incident");
        Instant now = Instant.now();
        return new SupportIncidentRecord(id, workspaceId, serviceProfileId, projectId, incidentNumber, title, description,
                severity, "RESOLVED", newImpactSummary, clientVisibleSummary, ownerUserId,
                detectedAt, acknowledgedAt, now, closedAt, version, createdAt, now);
    }
    public SupportIncidentRecord close() {
        if (!"RESOLVED".equals(status)) throw new IllegalStateException("Only RESOLVED can be closed");
        Instant now = Instant.now();
        return new SupportIncidentRecord(id, workspaceId, serviceProfileId, projectId, incidentNumber, title, description,
                severity, "CLOSED", impactSummary, clientVisibleSummary, ownerUserId,
                detectedAt, acknowledgedAt, resolvedAt, now, version, createdAt, now);
    }
}
