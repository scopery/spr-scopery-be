package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class IamAccessGrantPermissionActionKey implements Serializable {

    private UUID grantId;
    private UUID permissionActionId;

    public IamAccessGrantPermissionActionKey() {}

    public IamAccessGrantPermissionActionKey(UUID grantId, UUID permissionActionId) {
        this.grantId = grantId;
        this.permissionActionId = permissionActionId;
    }

    public UUID getGrantId() { return grantId; }
    public void setGrantId(UUID grantId) { this.grantId = grantId; }
    public UUID getPermissionActionId() { return permissionActionId; }
    public void setPermissionActionId(UUID permissionActionId) { this.permissionActionId = permissionActionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IamAccessGrantPermissionActionKey that)) return false;
        return Objects.equals(grantId, that.grantId)
                && Objects.equals(permissionActionId, that.permissionActionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantId, permissionActionId);
    }
}
