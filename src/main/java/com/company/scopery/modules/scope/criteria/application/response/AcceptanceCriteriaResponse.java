package com.company.scopery.modules.scope.criteria.application.response;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import java.time.Instant; import java.util.UUID;
public record AcceptanceCriteriaResponse(UUID id, UUID deliverableId, UUID projectId, String type, String title,
        String description, boolean mandatory, String status, Instant createdAt) {
    public static AcceptanceCriteriaResponse from(AcceptanceCriteria c) {
        return new AcceptanceCriteriaResponse(c.id(), c.deliverableId(), c.projectId(), c.type(), c.title(),
                c.description(), c.mandatory(), c.status().name(), c.createdAt());
    }
}
