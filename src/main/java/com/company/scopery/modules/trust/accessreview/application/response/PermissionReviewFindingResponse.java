package com.company.scopery.modules.trust.accessreview.application.response;
import com.company.scopery.modules.trust.accessreview.domain.model.PermissionReviewFinding;
import java.time.Instant; import java.util.UUID;
public record PermissionReviewFindingResponse(UUID id, UUID workspaceId, UUID campaignId,
        String findingType, String severity, String recommendation, String status,
        Instant resolvedAt, Instant createdAt) {
    public static PermissionReviewFindingResponse from(PermissionReviewFinding f) {
        return new PermissionReviewFindingResponse(f.id(), f.workspaceId(), f.campaignId(),
                f.findingType(), f.severity(), f.recommendation(), f.status(),
                f.resolvedAt(), f.createdAt());
    }
}
