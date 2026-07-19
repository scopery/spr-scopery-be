package com.company.scopery.modules.documenthub.share.domain.model;
import java.util.*;
public interface DocumentShareRepository {
    DocumentShare save(DocumentShare entity);
    Optional<DocumentShare> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentShare> findByProjectIdAndDocumentId(UUID projectId, UUID documentId);
}
