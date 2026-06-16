package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class IamAccessGrantRightKey implements Serializable {

    private UUID grantId;
    private UUID rightId;

    public IamAccessGrantRightKey() {}

    public IamAccessGrantRightKey(UUID grantId, UUID rightId) {
        this.grantId = grantId;
        this.rightId = rightId;
    }

    public UUID getGrantId() { return grantId; }
    public void setGrantId(UUID grantId) { this.grantId = grantId; }
    public UUID getRightId() { return rightId; }
    public void setRightId(UUID rightId) { this.rightId = rightId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IamAccessGrantRightKey that)) return false;
        return Objects.equals(grantId, that.grantId) && Objects.equals(rightId, that.rightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantId, rightId);
    }
}
