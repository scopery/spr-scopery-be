package com.company.scopery.modules.scope.deliverable.application.response;
import com.company.scopery.modules.scope.deliverable.domain.model.Deliverable;
import java.time.Instant; import java.util.UUID;
public record DeliverableResponse(UUID id, UUID projectId, String type, String code, String title, String status,
        boolean acceptanceRequired, Instant acceptedAt, UUID acceptedBy, Instant createdAt) {
    public static DeliverableResponse from(Deliverable d) {
        return new DeliverableResponse(d.id(), d.projectId(), d.type().name(), d.code(), d.title(), d.status().name(),
                d.acceptanceRequired(), d.acceptedAt(), d.acceptedBy(), d.createdAt());
    }
}
