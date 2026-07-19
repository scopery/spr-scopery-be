package com.company.scopery.modules.knowledge.documenttypefield.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentTypeFieldRepository {
    DocumentTypeField save(DocumentTypeField field);

    Optional<DocumentTypeField> findById(UUID id);

    List<DocumentTypeField> findByDocumentTypeId(UUID documentTypeId);

    boolean existsByDocumentTypeIdAndFieldKey(UUID documentTypeId, String fieldKey);

    void saveAll(List<DocumentTypeField> fields);
}
