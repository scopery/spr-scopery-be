package com.company.scopery.modules.traceability.requirementversion.application.response;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersion;
import java.time.Instant; import java.util.UUID;
public record RequirementVersionResponse(UUID id, UUID requirementId, UUID workspaceId, int versionNumber, String title,
                                         String description, String changeSummary, UUID createdByUserId, Instant createdAt) {
    public static RequirementVersionResponse from(RequirementVersion e) {
        return new RequirementVersionResponse(e.id(), e.requirementId(), e.workspaceId(), e.versionNumber(), e.title(),
                e.description(), e.changeSummary(), e.createdByUserId(), e.createdAt());
    }
}
