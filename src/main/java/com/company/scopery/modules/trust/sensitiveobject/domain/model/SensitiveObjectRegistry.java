package com.company.scopery.modules.trust.sensitiveobject.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SensitiveObjectRegistry(
        UUID id,
        UUID workspaceId,
        String objectTypeCode,
        String classification,
        boolean accessLoggingRequired,
        boolean exportReasonRequired,
        boolean searchIndexAllowed,
        boolean enabled,
        int version,
        Instant createdAt) {

    public static SensitiveObjectRegistry create(
            UUID workspaceId, String objectTypeCode, String classification) {
        return new SensitiveObjectRegistry(
                UUID.randomUUID(), workspaceId, objectTypeCode, classification,
                true, false, false, true, 0, Instant.now());
    }

    public SensitiveObjectRegistry update(String classification, boolean exportReasonRequired, boolean searchIndexAllowed) {
        return new SensitiveObjectRegistry(
                id, workspaceId, objectTypeCode, classification,
                accessLoggingRequired, exportReasonRequired, searchIndexAllowed, enabled, version, createdAt);
    }

    public SensitiveObjectRegistry deactivate() {
        return new SensitiveObjectRegistry(
                id, workspaceId, objectTypeCode, classification,
                accessLoggingRequired, exportReasonRequired, searchIndexAllowed, false, version, createdAt);
    }
}
