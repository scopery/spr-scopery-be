package com.company.scopery.modules.servicesupport.problem.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportProblemRecord(UUID id, UUID workspaceId, UUID projectId, UUID serviceProfileId,
        String problemNumber, String title, String description, String status,
        String rootCauseSummary, String workaround, UUID ownerUserId,
        Instant resolvedAt, UUID resolvedBy, Instant closedAt, UUID closedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportProblemRecord create(UUID workspaceId, UUID projectId, String title) {
        Instant now = Instant.now();
        String number = "PRB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new SupportProblemRecord(UUID.randomUUID(), workspaceId, projectId, null, number, title, null,
                "OPEN", null, null, null, null, null, null, null, 0, now, now);
    }
    public SupportProblemRecord resolve(String rootCause, String wa, UUID resolver) {
        Instant now = Instant.now();
        return new SupportProblemRecord(id, workspaceId, projectId, serviceProfileId, problemNumber, title, description,
                "RESOLVED", rootCause, wa, ownerUserId, now, resolver, closedAt, closedBy, version, createdAt, now);
    }
    public SupportProblemRecord close(UUID closer) {
        if (!"RESOLVED".equals(status)) throw new IllegalStateException("Only RESOLVED can be closed");
        Instant now = Instant.now();
        return new SupportProblemRecord(id, workspaceId, projectId, serviceProfileId, problemNumber, title, description,
                "CLOSED", rootCauseSummary, workaround, ownerUserId, resolvedAt, resolvedBy, now, closer, version, createdAt, now);
    }
}
