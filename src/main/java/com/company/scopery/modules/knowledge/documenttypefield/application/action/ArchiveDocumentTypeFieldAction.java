package com.company.scopery.modules.knowledge.documenttypefield.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldPlatformPublisher;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveDocumentTypeFieldAction {

    private final DocumentTypeFieldRepository fieldRepository;
    private final DocumentTypeFieldAccessGuard accessGuard;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypeFieldPlatformPublisher platformPublisher;

    public ArchiveDocumentTypeFieldAction(DocumentTypeFieldRepository fieldRepository,
                                          DocumentTypeFieldAccessGuard accessGuard,
                                          KnowledgeActivityLogger activityLogger,
                                          DocumentTypeFieldPlatformPublisher platformPublisher) {
        this.fieldRepository = fieldRepository;
        this.accessGuard = accessGuard;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeFieldResponse execute(UUID documentTypeId, UUID fieldId) {
        DocumentType dt = accessGuard.requireDocumentType(documentTypeId);
        accessGuard.requireWrite(dt, IamAuthorities.DOCUMENT_TYPE_FIELD_ARCHIVE);
        DocumentTypeField field = findOwned(documentTypeId, fieldId);
        if (field.systemField()) {
            throw KnowledgeExceptions.documentTypeFieldSystemCannotArchive(field.fieldKey().value());
        }
        DocumentTypeField saved = fieldRepository.save(field.archive());
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE_FIELD, saved.id(),
                KnowledgeActivityActions.ARCHIVE_DOCUMENT_TYPE_FIELD,
                "Document type field archived: " + saved.fieldKey().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_FIELD_ARCHIVED");
        return DocumentTypeFieldResponse.from(saved);
    }

    private DocumentTypeField findOwned(UUID documentTypeId, UUID fieldId) {
        DocumentTypeField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeFieldNotFound(fieldId));
        if (!field.documentTypeId().equals(documentTypeId)) {
            throw KnowledgeExceptions.documentTypeFieldPathMismatch(documentTypeId, fieldId);
        }
        return field;
    }
}
