package com.company.scopery.modules.iam.resource.domain.model;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;

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
        UUID organizationId,
        UUID workspaceId,
        IamResourceVisibility visibility,
        UUID parentResourceId,
        IamResourceStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static IamAuthResource create(IamResourceCode code, IamResourceType resourceType,
                                          String name, String description) {
        Instant now = Instant.now();
        return new IamAuthResource(UUID.randomUUID(), code, resourceType, name, description,
                null, null, null, null, null, null,
                IamResourceStatus.ACTIVE, 0, now, now);
    }

    public static IamAuthResource createWithOwnership(IamResourceCode code, IamResourceType resourceType,
                                                       String name, String description,
                                                       UUID refId, UUID ownerUserId, UUID workspaceId,
                                                       IamResourceVisibility visibility,
                                                       UUID parentResourceId) {
        return createWithOwnership(code, resourceType, name, description, refId, ownerUserId,
                null, workspaceId, visibility, parentResourceId);
    }

    public static IamAuthResource createWithOwnership(IamResourceCode code, IamResourceType resourceType,
                                                       String name, String description,
                                                       UUID refId, UUID ownerUserId, UUID organizationId,
                                                       UUID workspaceId, IamResourceVisibility visibility,
                                                       UUID parentResourceId) {
        Instant now = Instant.now();
        return new IamAuthResource(UUID.randomUUID(), code, resourceType, name, description,
                refId, ownerUserId, organizationId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.ACTIVE, 0, now, now);
    }

    public IamAuthResource update(String name, String description) {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, organizationId, workspaceId, visibility, parentResourceId,
                status, version, createdAt, Instant.now());
    }

    public IamAuthResource activate() {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, organizationId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.ACTIVE, version, createdAt, Instant.now());
    }

    public IamAuthResource deactivate() {
        return new IamAuthResource(id, code, resourceType, name, description,
                refId, ownerUserId, organizationId, workspaceId, visibility, parentResourceId,
                IamResourceStatus.INACTIVE, version, createdAt, Instant.now());
    }

    public boolean isOwnedBy(UUID userId) {
        return userId != null && userId.equals(ownerUserId);
    }
}
