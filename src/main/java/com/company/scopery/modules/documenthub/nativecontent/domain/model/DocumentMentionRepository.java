package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.util.List;
import java.util.UUID;

public interface DocumentMentionRepository {
    void saveAll(List<DocumentMention> mentions);
    void deleteByDocumentId(UUID documentId);
    List<DocumentMention> findByDocumentId(UUID documentId);
}
