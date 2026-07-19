package com.company.scopery.modules.traceability.requirement.application.response;
import com.company.scopery.modules.traceability.requirement.domain.model.Requirement; import java.time.Instant; import java.util.UUID;
public record RequirementResponse(UUID id, UUID projectId, UUID applicationId, String code, String title, String description, String requirementType, String priority, String status, Instant createdAt, Instant updatedAt) {
    public static RequirementResponse from(Requirement e){return new RequirementResponse(e.id(),e.projectId(),e.applicationId(),e.code(),e.title(),e.description(),e.requirementType().name(),e.priority().name(),e.status().name(),e.createdAt(),e.updatedAt());}
}
