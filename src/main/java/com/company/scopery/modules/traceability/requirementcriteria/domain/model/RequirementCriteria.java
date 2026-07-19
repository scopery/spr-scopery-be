package com.company.scopery.modules.traceability.requirementcriteria.domain.model;
import com.company.scopery.modules.traceability.requirementcriteria.domain.enums.RequirementCriteriaStatus;
import com.company.scopery.modules.traceability.requirementcriteria.domain.enums.RequirementCriteriaType;
import java.time.Instant; import java.util.UUID;
public record RequirementCriteria(UUID id, UUID requirementId, UUID workspaceId, String description,
                                  RequirementCriteriaType acceptanceType, RequirementCriteriaStatus status,
                                  int displayOrder, int version, Instant createdAt, Instant updatedAt) {
    public static RequirementCriteria create(UUID requirementId, UUID workspaceId, String description,
                                             RequirementCriteriaType acceptanceType, int displayOrder) {
        Instant now = Instant.now();
        return new RequirementCriteria(UUID.randomUUID(), requirementId, workspaceId, description, acceptanceType,
                RequirementCriteriaStatus.PENDING, displayOrder, 0, now, now);
    }
}
