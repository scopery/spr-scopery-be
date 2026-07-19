package com.company.scopery.modules.documenthub.folder.domain.model;
import java.util.*;
public interface DocumentFolderRepository {
    DocumentFolder save(DocumentFolder entity);
    Optional<DocumentFolder> findByIdAndProjectId(UUID id, UUID projectId);
    List<DocumentFolder> findByProjectId(UUID projectId);
}
