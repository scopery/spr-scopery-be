package com.company.scopery.modules.governance.objecttype.domain.model;
import java.time.Instant; import java.util.UUID;
public record GovernedObjectType(UUID id, String objectTypeCode, boolean ownershipSupported,
        boolean versioningSupported, boolean lockingSupported, boolean restoreSupported,
        boolean enabled, int version, Instant createdAt, Instant updatedAt) {
    public static GovernedObjectType create(String code, boolean ownership, boolean versioning, boolean locking, boolean restore) {
        return new GovernedObjectType(UUID.randomUUID(), code, ownership, versioning, locking, restore, true, 0, null, null);
    }
}
