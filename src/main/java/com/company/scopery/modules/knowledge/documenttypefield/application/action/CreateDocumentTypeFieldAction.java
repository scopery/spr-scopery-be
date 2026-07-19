package com.company.scopery.modules.knowledge.documenttypefield.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.CreateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldAccessGuard;
import com.company.scopery.modules.knowledge.documenttypefield.application.support.DocumentTypeFieldPlatformPublisher;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeFieldRepository;
import com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject.DocumentTypeFieldKey;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDocumentTypeFieldAction {

    private final DocumentTypeFieldRepository fieldRepository;
    private final DocumentTypeFieldAccessGuard accessGuard;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypeFieldPlatformPublisher platformPublisher;

    public CreateDocumentTypeFieldAction(DocumentTypeFieldRepository fieldRepository,
                                         DocumentTypeFieldAccessGuard accessGuard,
                                         KnowledgeActivityLogger activityLogger,
                                         DocumentTypeFieldPlatformPublisher platformPublisher) {
        this.fieldRepository = fieldRepository;
        this.accessGuard = accessGuard;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeFieldResponse execute(CreateDocumentTypeFieldCommand command) {
        DocumentType dt = accessGuard.requireDocumentType(command.documentTypeId());
        accessGuard.requireWrite(dt, IamAuthorities.DOCUMENT_TYPE_FIELD_CREATE);

        DocumentTypeFieldKey key;
        try {
            key = DocumentTypeFieldKey.of(command.fieldKey());
        } catch (IllegalArgumentException e) {
            throw KnowledgeExceptions.documentTypeFieldInvalidKey(e.getMessage());
        }

        if (fieldRepository.existsByDocumentTypeIdAndFieldKey(command.documentTypeId(), key.value())) {
            throw KnowledgeExceptions.documentTypeFieldKeyAlreadyExists(key.value());
        }

        DocumentTypeFieldDataType dataType = parseDataType(command.dataType());
        validateOptions(dataType, command.optionsJson());
        if (!platformPublisher.isValidJson(command.validationJson())
                || !platformPublisher.isValidJson(command.optionsJson())
                || !platformPublisher.isValidJson(command.defaultValueJson())) {
            throw KnowledgeExceptions.documentTypeInvalidMetadataSchema();
        }

        DocumentTypeField saved = fieldRepository.save(DocumentTypeField.create(
                command.documentTypeId(), key, command.label(), command.description(), dataType,
                command.required(), command.systemField(), command.optionsJson(),
                command.validationJson(), command.defaultValueJson(), command.displayOrder()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE_FIELD, saved.id(),
                KnowledgeActivityActions.CREATE_DOCUMENT_TYPE_FIELD,
                "Document type field created: " + saved.fieldKey().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_FIELD_CREATED");
        return DocumentTypeFieldResponse.from(saved);
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

    private void validateOptions(DocumentTypeFieldDataType dataType, String optionsJson) {
        if (dataType.requiresOptions() && (optionsJson == null || optionsJson.isBlank())) {
            throw KnowledgeExceptions.documentTypeFieldOptionsRequired();
        }
    }
}
