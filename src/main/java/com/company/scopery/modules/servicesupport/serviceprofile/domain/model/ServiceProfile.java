package com.company.scopery.modules.servicesupport.serviceprofile.domain.model;
import java.time.Instant; import java.util.UUID;
public record ServiceProfile(UUID id, UUID workspaceId, String scopeType, UUID projectId,
        boolean portalIntakeEnabled, String status, Instant createdAt) {
    public static ServiceProfile create(UUID workspaceId, String scopeType, UUID projectId) {
        return new ServiceProfile(UUID.randomUUID(), workspaceId, scopeType, projectId, false, "ACTIVE", Instant.now());
    }
    public boolean enabled() { return "ACTIVE".equals(status); }
    public ServiceProfile deactivate() {
        return new ServiceProfile(id, workspaceId, scopeType, projectId, portalIntakeEnabled, "INACTIVE", createdAt);
    }
}
