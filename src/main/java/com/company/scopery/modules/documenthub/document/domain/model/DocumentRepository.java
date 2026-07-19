package com.company.scopery.modules.documenthub.document.domain.model;
import java.util.*;
public interface DocumentRepository {
    Document save(Document e);
    Optional<Document> findByIdAndProjectId(UUID id, UUID projectId);
    List<Document> findByProjectId(UUID projectId);
}
