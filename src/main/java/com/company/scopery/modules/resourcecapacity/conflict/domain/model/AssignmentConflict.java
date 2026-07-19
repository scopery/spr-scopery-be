package com.company.scopery.modules.resourcecapacity.conflict.domain.model;
import java.time.Instant; import java.util.UUID;
public record AssignmentConflict(UUID id, UUID workspaceId, UUID projectId, UUID resourceProfileId, String conflictType,
        String severity, String description, String status, Instant detectedAt, Instant acknowledgedAt, Instant resolvedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static AssignmentConflict detect(UUID workspaceId, UUID projectId, UUID resourceProfileId, String type, String severity, String description) {
        Instant now = Instant.now();
        return new AssignmentConflict(UUID.randomUUID(), workspaceId, projectId, resourceProfileId, type, severity, description,
                "OPEN", now, null, null, 0, now, now);
    }
    public AssignmentConflict acknowledge() {
        if (!"OPEN".equals(status)) throw new IllegalStateException("invalid");
        return new AssignmentConflict(id, workspaceId, projectId, resourceProfileId, conflictType, severity, description,
                "ACKNOWLEDGED", detectedAt, Instant.now(), null, version, createdAt, Instant.now());
    }
    public AssignmentConflict resolve() {
        if (!"OPEN".equals(status) && !"ACKNOWLEDGED".equals(status)) throw new IllegalStateException("invalid");
        return new AssignmentConflict(id, workspaceId, projectId, resourceProfileId, conflictType, severity, description,
                "RESOLVED", detectedAt, acknowledgedAt, Instant.now(), version, createdAt, Instant.now());
    }
}
