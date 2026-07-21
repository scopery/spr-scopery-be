package com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceProfileStatus;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceType;
import java.time.Instant; import java.util.UUID;
public record ResourceProfile(UUID id, UUID workspaceId, UUID linkedUserId, UUID linkedWorkspaceMemberId,
        UUID linkedTeamId, UUID linkedExternalContactId, ResourceType resourceType, String displayName,
        UUID primaryRoleId, UUID defaultCalendarId, UUID defaultRateCardId, String timezone,
        ResourceProfileStatus status, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static ResourceProfile create(UUID workspaceId, ResourceType type, String displayName, UUID linkedUserId,
                                         UUID linkedMemberId, UUID primaryRoleId) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() → persist() (not merge/optimistic-lock).
        return new ResourceProfile(UUID.randomUUID(), workspaceId, linkedUserId, linkedMemberId, null, null,
                type, displayName, primaryRoleId, null, null, null, ResourceProfileStatus.ACTIVE,
                null, null, 0, null, null);
    }
    public static ResourceProfile placeholder(UUID workspaceId, String displayName, UUID primaryRoleId) {
        return create(workspaceId, ResourceType.PLACEHOLDER_ROLE, displayName, null, null, primaryRoleId);
    }
    public ResourceProfile archive(UUID actorId) {
        if (status == ResourceProfileStatus.ARCHIVED) return this;
        return new ResourceProfile(id, workspaceId, linkedUserId, linkedWorkspaceMemberId, linkedTeamId,
                linkedExternalContactId, resourceType, displayName, primaryRoleId, defaultCalendarId, defaultRateCardId,
                timezone, ResourceProfileStatus.ARCHIVED, Instant.now(), actorId, version, createdAt, Instant.now());
    }
    public ResourceProfile withDisplayName(String name) {
        return new ResourceProfile(id, workspaceId, linkedUserId, linkedWorkspaceMemberId, linkedTeamId,
                linkedExternalContactId, resourceType, name, primaryRoleId, defaultCalendarId, defaultRateCardId,
                timezone, status, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
