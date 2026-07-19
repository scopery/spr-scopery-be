package com.company.scopery.modules.resourcecapacity.resourceprofile.application.response;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfile;
import java.time.Instant; import java.util.UUID;
public record ResourceProfileResponse(UUID id, UUID workspaceId, UUID linkedUserId, String resourceType, String displayName,
        UUID primaryRoleId, String status, Instant createdAt, Instant updatedAt) {
    public static ResourceProfileResponse from(ResourceProfile p) {
        return new ResourceProfileResponse(p.id(), p.workspaceId(), p.linkedUserId(), p.resourceType().name(),
                p.displayName(), p.primaryRoleId(), p.status().name(), p.createdAt(), p.updatedAt());
    }
}
