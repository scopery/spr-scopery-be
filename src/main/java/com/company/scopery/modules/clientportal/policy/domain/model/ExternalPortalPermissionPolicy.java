package com.company.scopery.modules.clientportal.policy.domain.model;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalPermissionPolicy(UUID id, UUID workspaceId, String code, String name,
                                             String description, String permissionsJson,
                                             int version, Instant createdAt, Instant updatedAt) {
    public static ExternalPortalPermissionPolicy create(UUID workspaceId, String code, String name,
                                                        String description, String permissionsJson) {
        Instant now = Instant.now();
        return new ExternalPortalPermissionPolicy(UUID.randomUUID(), workspaceId, code.toUpperCase().trim(),
                name.trim(), description, permissionsJson, 0, now, now);
    }
    public ExternalPortalPermissionPolicy update(String name, String description, String permissionsJson) {
        return new ExternalPortalPermissionPolicy(id, workspaceId, code, name.trim(), description,
                permissionsJson, version, createdAt, Instant.now());
    }
}
