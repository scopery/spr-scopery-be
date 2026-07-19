package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface DocumentContentRepository {
    DocumentContent save(DocumentContent content);
    Optional<DocumentContent> findByDocumentId(UUID documentId);
}
