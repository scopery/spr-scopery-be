package com.company.scopery.modules.trust.sensitivefield.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SensitiveFieldRegistry(
        UUID id,
        UUID workspaceId,
        String objectTypeCode,
        String fieldPath,
        String classification,
        String maskingStrategy,
        boolean accessLoggingRequired,
        boolean searchIndexAllowed,
        boolean exportAllowed,
        boolean enabled,
        int version,
        Instant createdAt) {

    public static SensitiveFieldRegistry create(
            UUID workspaceId, String objectTypeCode, String fieldPath,
            String classification, String maskingStrategy) {
        return new SensitiveFieldRegistry(
                UUID.randomUUID(), workspaceId, objectTypeCode, fieldPath, classification, maskingStrategy,
                true, false, false, true, 0, Instant.now());
    }

    public SensitiveFieldRegistry update(String classification, String maskingStrategy, boolean exportAllowed) {
        return new SensitiveFieldRegistry(
                id, workspaceId, objectTypeCode, fieldPath, classification, maskingStrategy,
                accessLoggingRequired, searchIndexAllowed, exportAllowed, enabled, version, createdAt);
    }

    public SensitiveFieldRegistry deactivate() {
        return new SensitiveFieldRegistry(
                id, workspaceId, objectTypeCode, fieldPath, classification, maskingStrategy,
                accessLoggingRequired, searchIndexAllowed, exportAllowed, false, version, createdAt);
    }
}
