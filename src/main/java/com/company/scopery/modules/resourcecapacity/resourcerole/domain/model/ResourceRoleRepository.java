package com.company.scopery.modules.resourcecapacity.resourcerole.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ResourceRoleRepository {
    ResourceRole save(ResourceRole e);
    Optional<ResourceRole> findById(UUID id);
    List<ResourceRole> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndRoleCode(UUID workspaceId, String code);
}
