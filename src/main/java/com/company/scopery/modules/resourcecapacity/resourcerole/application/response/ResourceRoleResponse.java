package com.company.scopery.modules.resourcecapacity.resourcerole.application.response;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRole;
import java.time.Instant; import java.util.UUID;
public record ResourceRoleResponse(UUID id, UUID workspaceId, String roleCode, String name, String status, Instant createdAt) {
    public static ResourceRoleResponse from(ResourceRole e) { return new ResourceRoleResponse(e.id(), e.workspaceId(), e.roleCode(), e.name(), e.status().name(), e.createdAt()); }
}
