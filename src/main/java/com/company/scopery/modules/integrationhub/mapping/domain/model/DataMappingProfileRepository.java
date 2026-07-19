package com.company.scopery.modules.integrationhub.mapping.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DataMappingProfileRepository {
    DataMappingProfile save(DataMappingProfile p);
    Optional<DataMappingProfile> findById(UUID id);
    Optional<DataMappingProfile> findByWorkspaceIdAndMappingCode(UUID workspaceId, String code);
    List<DataMappingProfile> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndMappingCode(UUID workspaceId, String code);
}
