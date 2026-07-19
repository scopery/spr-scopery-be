package com.company.scopery.modules.clientportal.feedback.domain.model;
import com.company.scopery.modules.clientportal.feedback.domain.enums.ClientFeedbackStatus;
import java.time.Instant; import java.util.UUID;
public record ClientFeedback(UUID id, UUID projectId, UUID workspaceId, String category, String title, String body, ClientFeedbackStatus status,
                             UUID submittedByPortalAccountId, int version, Instant createdAt, Instant updatedAt) {
    public static ClientFeedback create(UUID projectId, UUID workspaceId, String category, String title, String body, UUID submittedBy) {
        Instant now = Instant.now();
        return new ClientFeedback(UUID.randomUUID(), projectId, workspaceId, category, title, body, ClientFeedbackStatus.SUBMITTED, submittedBy, 0, now, now);
    }
}
