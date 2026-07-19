package com.company.scopery.modules.knowledge.documenttypefield.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.UpdateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldPlatformPublisher;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateDocumentTypeFieldAction {

    private final DocumentTypeFieldRepository fieldRepository;
    private final DocumentTypeFieldAccessGuard accessGuard;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypeFieldPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserService;

    public UpdateDocumentTypeFieldAction(DocumentTypeFieldRepository fieldRepository,
                                         DocumentTypeFieldAccessGuard accessGuard,
                                         KnowledgeActivityLogger activityLogger,
                                         DocumentTypeFieldPlatformPublisher platformPublisher,
                                         CurrentUserAuthorizationService currentUserService) {
        this.fieldRepository = fieldRepository;
        this.accessGuard = accessGuard;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public DocumentTypeFieldResponse execute(UpdateDocumentTypeFieldCommand command) {
        DocumentType dt = accessGuard.requireDocumentType(command.documentTypeId());
        accessGuard.requireWrite(dt, IamAuthorities.DOCUMENT_TYPE_FIELD_UPDATE);

        DocumentTypeField field = findOwned(command.documentTypeId(), command.fieldId());
        if (field.isArchived()) {
            throw KnowledgeExceptions.documentTypeFieldArchived(field.id());
        }

        DocumentTypeFieldDataType dataType = parseDataType(command.dataType());
        if (dataType.requiresOptions() && (command.optionsJson() == null || command.optionsJson().isBlank())) {
            throw KnowledgeExceptions.documentTypeFieldOptionsRequired();
        }
        if (!platformPublisher.isValidJson(command.validationJson())
                || !platformPublisher.isValidJson(command.optionsJson())
                || !platformPublisher.isValidJson(command.defaultValueJson())) {
            throw KnowledgeExceptions.documentTypeInvalidMetadataSchema();
        }

        DocumentTypeField updated = field.update(command.label(), command.description(), dataType,
                command.required(), command.optionsJson(), command.validationJson(),
                command.defaultValueJson(), command.displayOrder());
        DocumentTypeField saved = fieldRepository.save(updated);

        UUID actorId = currentUserService.resolveCurrentUser().id();
        if (field.dataType() != saved.dataType() || field.required() != saved.required()) {
            platformPublisher.auditSchemaChange(actorId, field, saved, "Document type field schema changed");
        }

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE_FIELD, saved.id(),
                KnowledgeActivityActions.UPDATE_DOCUMENT_TYPE_FIELD,
                "Document type field updated: " + saved.fieldKey().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_FIELD_UPDATED");
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

    private DocumentTypeFieldDataType parseDataType(String raw) {
        try {
            return KnowledgeEnumParser.parseRequired(
                    DocumentTypeFieldDataType.class, raw,
                    KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_INVALID_DATA_TYPE.code(), "dataType");
        } catch (Exception e) {
            throw KnowledgeExceptions.documentTypeFieldInvalidDataType(raw);
        }
    }
}
