package com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ResourceProfileRepository {
    ResourceProfile save(ResourceProfile profile);
    Optional<ResourceProfile> findById(UUID id);
    List<ResourceProfile> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId);
    Optional<ResourceProfile> findByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId);
}
