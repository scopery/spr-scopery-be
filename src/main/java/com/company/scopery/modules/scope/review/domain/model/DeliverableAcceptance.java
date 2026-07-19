package com.company.scopery.modules.scope.review.domain.model;
import com.company.scopery.modules.scope.review.domain.enums.AcceptanceOutcome;
import java.time.Instant; import java.util.UUID;
public record DeliverableAcceptance(UUID id, UUID deliverableId, UUID projectId, AcceptanceOutcome outcome,
        String notes, UUID acceptedBy, Instant acceptedAt, int version, Instant createdAt) {
    public static DeliverableAcceptance recordAccepted(UUID deliverableId, UUID projectId, UUID actorId, String notes) {
        return new DeliverableAcceptance(UUID.randomUUID(), deliverableId, projectId, AcceptanceOutcome.ACCEPTED,
                notes, actorId, Instant.now(), 0, Instant.now());
    }
}
