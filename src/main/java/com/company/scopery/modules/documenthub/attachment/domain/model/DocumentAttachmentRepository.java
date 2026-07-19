package com.company.scopery.modules.documenthub.attachment.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentAttachmentRepository {
    DocumentAttachment save(DocumentAttachment attachment);
    Optional<DocumentAttachment> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentAttachment> findByDocumentId(UUID documentId);
}
