package com.company.scopery.modules.integrationhub.mapping.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ExternalIdMappingRepository {
    ExternalIdMapping save(ExternalIdMapping m);
    Optional<ExternalIdMapping> findById(UUID id);
    List<ExternalIdMapping> findByWorkspaceId(UUID workspaceId);
    List<ExternalIdMapping> findByWorkspaceIdAndScoperyObjectTypeAndScoperyObjectId(UUID workspaceId, String type, UUID objectId);
}
