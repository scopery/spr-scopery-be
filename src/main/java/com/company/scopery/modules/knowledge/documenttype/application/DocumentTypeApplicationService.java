package com.company.scopery.modules.knowledge.documenttype.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.query.SearchDocumentTypeQuery;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeSortFields;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentTypeApplicationService {

    private final DocumentTypeRepository documentTypeRepository;
    private final KnowledgeActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;

    public DocumentTypeApplicationService(DocumentTypeRepository documentTypeRepository,
                                           KnowledgeActivityLogger activityLogger,
                                           CurrentUserAuthorizationService currentUserService) {
        this.documentTypeRepository = documentTypeRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public DocumentTypeResponse createSystemDocumentType(CreateDocumentTypeCommand command) {
        if (command.workspaceId() != null) {
            throw KnowledgeExceptions.documentTypeSystemScopeMustNotHaveWorkspaceId();
        }
        DocumentTypeCode code = DocumentTypeCode.of(command.code());
        if (documentTypeRepository.existsByCodeAndScopeSystem(code)) {
            throw KnowledgeExceptions.documentTypeCodeAlreadyExists(code.value());
        }
        DocumentType saved = documentTypeRepository.save(
                DocumentType.createSystem(code, command.name(), command.description()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.CREATE_DOCUMENT_TYPE,
                "System document type created: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional
    public DocumentTypeResponse createWorkspaceDocumentType(CreateDocumentTypeCommand command) {
        if (command.workspaceId() == null) {
            throw KnowledgeExceptions.documentTypeWorkspaceScopeRequiresWorkspaceId();
        }
        DocumentTypeCode code = DocumentTypeCode.of(command.code());
        if (documentTypeRepository.existsByCodeAndWorkspaceId(code, command.workspaceId())) {
            throw KnowledgeExceptions.documentTypeWorkspaceCodeAlreadyExists(code.value(), command.workspaceId());
        }
        DocumentType saved = documentTypeRepository.save(
                DocumentType.createWorkspace(code, command.name(), command.description(), command.workspaceId()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.CREATE_DOCUMENT_TYPE,
                "Workspace document type created: " + saved.code().value() + " in workspace " + command.workspaceId());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional
    public DocumentTypeResponse updateDocumentType(UpdateDocumentTypeCommand command) {
        DocumentType dt = findOrThrow(command.id());
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        DocumentType saved = documentTypeRepository.save(dt.update(command.name(), command.description()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.UPDATE_DOCUMENT_TYPE,
                "Document type updated: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional
    public DocumentTypeResponse activateDocumentType(UUID id) {
        DocumentType dt = findOrThrow(id);
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        DocumentType saved = documentTypeRepository.save(dt.activate());
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.ACTIVATE_DOCUMENT_TYPE,
                "Document type activated: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional
    public DocumentTypeResponse deactivateDocumentType(UUID id) {
        DocumentType dt = findOrThrow(id);
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        DocumentType saved = documentTypeRepository.save(dt.deactivate());
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.DEACTIVATE_DOCUMENT_TYPE,
                "Document type deactivated: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional
    public DocumentTypeResponse softDeleteDocumentType(UUID id) {
        DocumentType dt = findOrThrow(id);
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        if (dt.isSystem()) {
            throw KnowledgeExceptions.documentTypeSystemCannotBeDeleted(dt.code().value());
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        DocumentType saved = documentTypeRepository.save(dt.softDelete(actorId));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.SOFT_DELETE_DOCUMENT_TYPE,
                "Document type soft-deleted: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public DocumentTypeResponse getDocumentType(UUID id) {
        return DocumentTypeResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<DocumentTypeResponse> searchDocumentTypes(SearchDocumentTypeQuery query) {
        DocumentTypeScope scope = KnowledgeEnumParser.parseOptional(
                DocumentTypeScope.class, query.documentScope(),
                KnowledgeErrorCatalog.INVALID_DOCUMENT_TYPE_SCOPE.code(), "documentScope");
        DocumentTypeStatus status = KnowledgeEnumParser.parseOptional(
                DocumentTypeStatus.class, query.status(),
                KnowledgeErrorCatalog.INVALID_DOCUMENT_TYPE_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, KnowledgeSortFields.CREATED_AT));
        return documentTypeRepository.findAll(query.keyword(), query.workspaceId(), scope,
                        status, query.includeDeleted(), pageable)
                .map(DocumentTypeResponse::from);
    }

    private DocumentType findOrThrow(UUID id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
    }
}
