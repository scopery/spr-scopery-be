package com.company.scopery.modules.documenthub.version.domain.model;
import java.util.*;
public interface DocumentVersionRepository {
    DocumentVersion save(DocumentVersion entity);
    Optional<DocumentVersion> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentVersion> findByProjectIdAndDocumentId(UUID projectId, UUID documentId);
    int nextVersionNumber(UUID documentId);
}
