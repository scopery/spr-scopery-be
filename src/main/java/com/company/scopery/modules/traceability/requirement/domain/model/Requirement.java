package com.company.scopery.modules.traceability.requirement.domain.model;
import com.company.scopery.modules.traceability.requirement.domain.enums.*;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import java.time.Instant; import java.util.UUID;
public record Requirement(UUID id, UUID projectId, UUID workspaceId, UUID applicationId, String code, String title, String description,
        RequirementType requirementType, RequirementPriority priority, RequirementStatus status, UUID ownerUserId,
        int currentVersionNumber, Instant archivedAt, UUID archivedBy,
        UUID functionalItemId, UUID nonFunctionalItemId,
        Integer version, Instant createdAt, Instant updatedAt) {
    public static Requirement create(UUID projectId, UUID workspaceId, UUID applicationId, String code, String title, String description,
                                     RequirementType type, RequirementPriority priority,
                                     UUID functionalItemId, UUID nonFunctionalItemId) {
        Instant now = Instant.now();
        return new Requirement(UUID.randomUUID(), projectId, workspaceId, applicationId, code, title, description, type, priority,
                RequirementStatus.DRAFT, null, 1, null, null, functionalItemId, nonFunctionalItemId, null, now, now);
    }
    public Requirement approve() {
        return new Requirement(id, projectId, workspaceId, applicationId, code, title, description, requirementType, priority,
                RequirementStatus.APPROVED, ownerUserId, currentVersionNumber, archivedAt, archivedBy,
                functionalItemId, nonFunctionalItemId, version, createdAt, Instant.now());
    }
    public Requirement reject() {
        if (status != RequirementStatus.READY && status != RequirementStatus.APPROVED)
            throw TraceabilityExceptions.requirementInvalidStatus(status.name());
        return new Requirement(id, projectId, workspaceId, applicationId, code, title, description, requirementType, priority,
                RequirementStatus.REJECTED, ownerUserId, currentVersionNumber, archivedAt, archivedBy,
                functionalItemId, nonFunctionalItemId, version, createdAt, Instant.now());
    }
    public Requirement defer() {
        if (status != RequirementStatus.DRAFT && status != RequirementStatus.READY && status != RequirementStatus.APPROVED)
            throw TraceabilityExceptions.requirementInvalidStatus(status.name());
        return new Requirement(id, projectId, workspaceId, applicationId, code, title, description, requirementType, priority,
                RequirementStatus.DEFERRED, ownerUserId, currentVersionNumber, archivedAt, archivedBy,
                functionalItemId, nonFunctionalItemId, version, createdAt, Instant.now());
    }
    public Requirement markImplemented() {
        if (status != RequirementStatus.APPROVED)
            throw TraceabilityExceptions.requirementInvalidStatus(status.name());
        return new Requirement(id, projectId, workspaceId, applicationId, code, title, description, requirementType, priority,
                RequirementStatus.IMPLEMENTED, ownerUserId, currentVersionNumber, archivedAt, archivedBy,
                functionalItemId, nonFunctionalItemId, version, createdAt, Instant.now());
    }
    public Requirement archive() {
        if (status == RequirementStatus.ARCHIVED)
            throw TraceabilityExceptions.requirementInvalidStatus(status.name());
        Instant now = Instant.now();
        return new Requirement(id, projectId, workspaceId, applicationId, code, title, description, requirementType, priority,
                RequirementStatus.ARCHIVED, ownerUserId, currentVersionNumber, now, archivedBy,
                functionalItemId, nonFunctionalItemId, version, createdAt, now);
    }
    public Requirement update(String title, String description, RequirementPriority priority, RequirementType type,
                              UUID applicationId, UUID functionalItemId, UUID nonFunctionalItemId) {
        if (status == RequirementStatus.APPROVED)
            throw TraceabilityExceptions.requirementImmutable();
        return new Requirement(id, projectId, workspaceId,
                applicationId != null ? applicationId : this.applicationId,
                code,
                title != null && !title.isBlank() ? title.trim() : this.title,
                description != null ? description : this.description,
                type != null ? type : this.requirementType,
                priority != null ? priority : this.priority,
                status, ownerUserId, currentVersionNumber, archivedAt, archivedBy,
                functionalItemId != null ? functionalItemId : this.functionalItemId,
                nonFunctionalItemId != null ? nonFunctionalItemId : this.nonFunctionalItemId,
                version, createdAt, Instant.now());
    }
}
