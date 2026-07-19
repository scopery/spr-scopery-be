package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.util.List;
import java.util.UUID;

public interface DocumentBlockIndexRepository {
    void saveAll(List<DocumentBlockIndex> blocks);
    void deleteByDocumentId(UUID documentId);
    List<DocumentBlockIndex> findByDocumentId(UUID documentId);
}
