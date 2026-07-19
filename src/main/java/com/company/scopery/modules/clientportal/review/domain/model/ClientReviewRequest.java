package com.company.scopery.modules.clientportal.review.domain.model;
import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewStatus;
import java.time.Instant; import java.util.UUID;
public record ClientReviewRequest(UUID id, UUID projectId, UUID workspaceId, String targetType, UUID targetId, String title,
        ClientReviewStatus status, Instant dueAt, UUID requestedBy, UUID assignedPortalAccountId,
        int version, Instant createdAt, Instant updatedAt) {
    public static ClientReviewRequest create(UUID projectId, UUID workspaceId, String targetType, UUID targetId, String title, UUID requestedBy, UUID assigned) {
        Instant now = Instant.now();
        return new ClientReviewRequest(UUID.randomUUID(), projectId, workspaceId, targetType, targetId, title, ClientReviewStatus.DRAFT, null, requestedBy, assigned, 0, now, now);
    }
    public ClientReviewRequest decide() {
        if (status != ClientReviewStatus.SENT && status != ClientReviewStatus.VIEWED && status != ClientReviewStatus.OVERDUE) throw new IllegalStateException("invalid");
        return new ClientReviewRequest(id, projectId, workspaceId, targetType, targetId, title, ClientReviewStatus.RESPONDED, dueAt, requestedBy, assignedPortalAccountId, version, createdAt, Instant.now());
    }
}
