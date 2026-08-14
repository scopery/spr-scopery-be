package com.company.scopery.modules.traceability.screenspecdoc.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenSpecDocumentRepository {
    Optional<RegistryScreenSpecDocument> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    Optional<RegistryScreenSpecDocument> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndDocumentCode(UUID projectId, String documentCode);
    List<RegistryScreenSpecDocument> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    RegistryScreenSpecDocument save(RegistryScreenSpecDocument doc);
    void delete(UUID id);
}
