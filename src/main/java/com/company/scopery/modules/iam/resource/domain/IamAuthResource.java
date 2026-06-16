package com.company.scopery.modules.iam.resource.domain;

import java.time.Instant;
import java.util.UUID;

public record IamAuthResource(
        UUID id,
        IamResourceCode code,
        IamResourceType resourceType,
        String name,
        String description,
        UUID refId,
        UUID ownerUserId,
        UUID workspaceId,
        IamResourceVisibility visibility,
        UUID parentResourceId,
        IamResourceStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamAuthResource create(IamResourceCode code, IamResourceType resourceType,
                                          String name, String description) {
        Instant now = Instant.now();
        return new IamAuthResource(UUID.randomUUID(), code, resourceType, name, description,
                null, null, null, null, null,
                IamResourceStatus.ACTIVE, now, now);
    }

    public static IamAuthResource createWithOwnership(IamResourceCode code, IamResourceType resourceType,
                                                       String name, String description,
                                                       UUID refId, UUID ownerUserId, UUID workspaceId,
                                                       IamResourceVisibility visibility,
                                                       UUID parentResourceId) {
        Instant now = Instant.now();
        return new IamAuthResource(UUID.randomUUID(), code, resourceType, name, description,
                refId, ownerUserId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.ACTIVE, now, now);
    }

    public IamAuthResource update(String name, String description) {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, workspaceId, visibility, parentResourceId,
                status, createdAt, Instant.now());
    }

    public IamAuthResource activate() {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.ACTIVE, createdAt, Instant.now());
    }

    public IamAuthResource deactivate() {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.INACTIVE, createdAt, Instant.now());
    }

    public boolean isOwnedBy(UUID userId) {
        return userId != null && userId.equals(ownerUserId);
    }
}
