package com.company.scopery.modules.traceability.specdocrevision.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrySpecDocRevisionRepository {
    Optional<RegistrySpecDocRevision> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistrySpecDocRevision> findByDocumentIdOrderByDisplayOrderAsc(UUID documentId);
    RegistrySpecDocRevision save(RegistrySpecDocRevision revision);
    void delete(UUID id);
}
