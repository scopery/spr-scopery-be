package com.company.scopery.modules.traceability.requirementcriteria.application.response;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteria;
import java.time.Instant; import java.util.UUID;
public record RequirementCriteriaResponse(UUID id, UUID requirementId, UUID workspaceId, String description,
                                          String acceptanceType, String status, int displayOrder, Instant createdAt) {
    public static RequirementCriteriaResponse from(RequirementCriteria e) {
        return new RequirementCriteriaResponse(e.id(), e.requirementId(), e.workspaceId(), e.description(),
                e.acceptanceType().name(), e.status().name(), e.displayOrder(), e.createdAt());
    }
}
