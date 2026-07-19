package com.company.scopery.modules.knowledge.documenttypefield.application.service;

import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentTypeFieldQueryService {

    private final DocumentTypeFieldRepository fieldRepository;
    private final DocumentTypeFieldAccessGuard accessGuard;

    public DocumentTypeFieldQueryService(DocumentTypeFieldRepository fieldRepository,
                                         DocumentTypeFieldAccessGuard accessGuard) {
        this.fieldRepository = fieldRepository;
        this.accessGuard = accessGuard;
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeFieldResponse> listFields(UUID documentTypeId) {
        DocumentType dt = accessGuard.requireDocumentType(documentTypeId, false);
        accessGuard.requireView(dt);
        return fieldRepository.findByDocumentTypeId(documentTypeId).stream()
                .map(DocumentTypeFieldResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentTypeFieldResponse getField(UUID documentTypeId, UUID fieldId) {
        DocumentType dt = accessGuard.requireDocumentType(documentTypeId, false);
        accessGuard.requireView(dt);
        DocumentTypeField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeFieldNotFound(fieldId));
        if (!field.documentTypeId().equals(documentTypeId)) {
            throw KnowledgeExceptions.documentTypeFieldPathMismatch(documentTypeId, fieldId);
        }
        return DocumentTypeFieldResponse.from(field);
    }
}
