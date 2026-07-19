package com.company.scopery.modules.traceability.requirementversion.domain.model;
import java.time.Instant; import java.util.UUID;
public record RequirementVersion(UUID id, UUID requirementId, UUID workspaceId, int versionNumber, String title,
                                 String description, String changeSummary, UUID createdByUserId, int version, Instant createdAt) {
    public static RequirementVersion create(UUID requirementId, UUID workspaceId, int versionNumber, String title,
                                            String description, String changeSummary, UUID createdByUserId) {
        return new RequirementVersion(UUID.randomUUID(), requirementId, workspaceId, versionNumber, title, description,
                changeSummary, createdByUserId, 0, Instant.now());
    }
}
