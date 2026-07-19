package com.company.scopery.modules.traceability.requirementsource.domain.model;
import com.company.scopery.modules.traceability.requirementsource.domain.enums.RequirementSourceStatus;
import java.time.Instant; import java.util.UUID;
public record RequirementSource(UUID id, UUID requirementId, UUID workspaceId, String sourceType, String sourceReference,
                                String description, RequirementSourceStatus status, int version, Instant createdAt) {
    public static RequirementSource create(UUID requirementId, UUID workspaceId, String sourceType, String sourceReference, String description) {
        return new RequirementSource(UUID.randomUUID(), requirementId, workspaceId, sourceType, sourceReference, description,
                RequirementSourceStatus.ACTIVE, 0, Instant.now());
    }
}
