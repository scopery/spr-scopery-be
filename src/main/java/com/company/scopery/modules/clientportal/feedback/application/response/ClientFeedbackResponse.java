package com.company.scopery.modules.clientportal.feedback.application.response;
import com.company.scopery.modules.clientportal.feedback.domain.model.ClientFeedback;
import java.time.Instant; import java.util.UUID;
public record ClientFeedbackResponse(UUID id, UUID projectId, String category, String title, String status, Instant createdAt) {
    public static ClientFeedbackResponse from(ClientFeedback e) {
        return new ClientFeedbackResponse(e.id(), e.projectId(), e.category(), e.title(), e.status().name(), e.createdAt());
    }
}
