package com.company.scopery.modules.trust.accessreview.domain.model;
import java.time.Instant; import java.util.UUID;
public record PermissionReviewFinding(UUID id, UUID workspaceId, UUID campaignId, String findingType, String severity,
        String recommendation, String status, Instant resolvedAt, int version, Instant createdAt) {
    public static PermissionReviewFinding open(UUID workspaceId, UUID campaignId, String type, String severity, String recommendation) {
        return new PermissionReviewFinding(UUID.randomUUID(), workspaceId, campaignId, type, severity, recommendation, "OPEN", null, 0, Instant.now());
    }
    public PermissionReviewFinding resolve() {
        // does not auto-revoke access
        return new PermissionReviewFinding(id, workspaceId, campaignId, findingType, severity, recommendation, "RESOLVED", Instant.now(), version, createdAt);
    }
    public PermissionReviewFinding dismiss() {
        return new PermissionReviewFinding(id, workspaceId, campaignId, findingType, severity, recommendation, "DISMISSED", resolvedAt, version, createdAt);
    }
}
