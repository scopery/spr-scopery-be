package com.company.scopery.modules.clientportal.review.application.response;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewRequest; import java.time.Instant; import java.util.UUID;
public record ClientReviewRequestResponse(UUID id, UUID projectId, String targetType, UUID targetId, String title, String status, Instant createdAt) {
    public static ClientReviewRequestResponse from(ClientReviewRequest e){return new ClientReviewRequestResponse(e.id(),e.projectId(),e.targetType(),e.targetId(),e.title(),e.status().name(),e.createdAt());}
}
