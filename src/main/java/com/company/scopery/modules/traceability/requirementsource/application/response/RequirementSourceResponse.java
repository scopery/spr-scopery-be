package com.company.scopery.modules.traceability.requirementsource.application.response;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSource;
import java.time.Instant; import java.util.UUID;
public record RequirementSourceResponse(UUID id, UUID requirementId, UUID workspaceId, String sourceType, String sourceReference,
                                        String description, String status, Instant createdAt) {
    public static RequirementSourceResponse from(RequirementSource e) {
        return new RequirementSourceResponse(e.id(), e.requirementId(), e.workspaceId(), e.sourceType(), e.sourceReference(),
                e.description(), e.status().name(), e.createdAt());
    }
}
