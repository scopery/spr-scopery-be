package com.company.scopery.modules.integrationhub.exportprofile.application.response;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfile;
import java.time.Instant; import java.util.UUID;
public record ExportProfileResponse(UUID id, UUID workspaceId, UUID connectionId, String profileCode, String name,
        String exportFormat, String targetDestination, String objectScope, String status,
        Instant createdAt, Instant updatedAt) {
    public static ExportProfileResponse from(ExportProfile p) {
        return new ExportProfileResponse(p.id(), p.workspaceId(), p.connectionId(), p.profileCode(), p.name(),
                p.exportFormat(), p.targetDestination(), p.objectScope(), p.status(), p.createdAt(), p.updatedAt());
    }
}
