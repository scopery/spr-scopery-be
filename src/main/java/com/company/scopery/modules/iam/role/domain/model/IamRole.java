package com.company.scopery.modules.iam.role.domain.model;

import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;

import java.time.Instant;
import java.util.UUID;

public record IamRole(
        UUID id,
        IamRoleCode code,
        String name,
        String description,
        IamRoleStatus status,
        IamRoleScope roleScope,
        IamRoleSource roleSource,
        UUID workspaceId,
        UUID parentRoleId,
        UUID deletedBy,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static IamRole createSystem(IamRoleCode code, String name, String description,
                                       IamRoleSource source, UUID parentRoleId) {
        Instant now = Instant.now();
        return new IamRole(UUID.randomUUID(), code, name, description,
                IamRoleStatus.ACTIVE, IamRoleScope.SYSTEM, source,
                null, parentRoleId, null, null, now, now);
    }

    public static IamRole createWorkspace(IamRoleCode code, String name, String description,
                                          UUID workspaceId, UUID parentRoleId) {
        Instant now = Instant.now();
        return new IamRole(UUID.randomUUID(), code, name, description,
                IamRoleStatus.ACTIVE, IamRoleScope.WORKSPACE, IamRoleSource.WORKSPACE_CUSTOM,
                workspaceId, parentRoleId, null, null, now, now);
    }

    public IamRole update(String name, String description) {
        return new IamRole(id, code, name, description, status, roleScope, roleSource,
                workspaceId, parentRoleId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public IamRole activate() {
        return new IamRole(id, code, name, description, IamRoleStatus.ACTIVE, roleScope, roleSource,
                workspaceId, parentRoleId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public IamRole deactivate() {
        return new IamRole(id, code, name, description, IamRoleStatus.INACTIVE, roleScope, roleSource,
                workspaceId, parentRoleId, deletedBy, deletedAt, createdAt, Instant.now());
    }

    public IamRole softDelete(UUID actorId) {
        Instant now = Instant.now();
        return new IamRole(id, code, name, description, IamRoleStatus.DELETED, roleScope, roleSource,
                workspaceId, parentRoleId, actorId, now, createdAt, now);
    }

    public boolean isSystem() {
        return roleScope == IamRoleScope.SYSTEM;
    }

    public boolean isDeleted() {
        return status == IamRoleStatus.DELETED;
    }
}
