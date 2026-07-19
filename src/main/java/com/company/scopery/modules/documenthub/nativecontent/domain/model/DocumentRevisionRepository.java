package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRevisionRepository {
    DocumentRevision save(DocumentRevision revision);
    Optional<DocumentRevision> findByDocumentIdAndRevisionNo(UUID documentId, long revisionNo);
    Page<DocumentRevision> findByDocumentId(UUID documentId, Pageable pageable);
}
