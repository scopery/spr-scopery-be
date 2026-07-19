package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.application.support.DocumentTypePlatformPublisher;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypePlatformPublisher platformPublisher;

    public CreateDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                    WorkspaceRepository workspaceRepository,
                                    CurrentUserAuthorizationService currentUserAuthorizationService,
                                    WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                    IamSystemAuthorizationService systemAuthorizationService,
                                    KnowledgeActivityLogger activityLogger,
                                    DocumentTypePlatformPublisher platformPublisher) {
        this.documentTypeRepository = documentTypeRepository;
        this.workspaceRepository = workspaceRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeResponse execute(CreateDocumentTypeCommand command) {
        DocumentTypeScope scope = KnowledgeEnumParser.parseRequired(
                DocumentTypeScope.class,
                command.documentScope() != null ? command.documentScope() : "SYSTEM",
                KnowledgeErrorCatalog.INVALID_DOCUMENT_TYPE_SCOPE.code(),
                "documentScope");
        DocumentClassification classification = parseClassification(command.defaultClassification());
        if (!platformPublisher.isValidJson(command.metadataSchemaJson())) {
            throw KnowledgeExceptions.documentTypeInvalidMetadataSchema();
        }

        DocumentTypeCode code = DocumentTypeCode.of(command.code());
        DocumentType created = switch (scope) {
            case SYSTEM -> createSystem(command, code, classification);
            case ORGANIZATION -> createOrganization(command, code, classification);
            case WORKSPACE -> createWorkspace(command, code, classification);
        };

        DocumentType saved = documentTypeRepository.save(created);
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.CREATE_DOCUMENT_TYPE,
                "Document type created: " + saved.code().value() + " scope=" + scope.name());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_CREATED");
        return DocumentTypeResponse.from(saved);
    }

    private DocumentType createSystem(CreateDocumentTypeCommand command, DocumentTypeCode code,
                                      DocumentClassification classification) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        if (command.workspaceId() != null || command.organizationId() != null) {
            throw KnowledgeExceptions.documentTypeInvalidScope(
                    "SYSTEM scope must not have organizationId or workspaceId");
        }
        if (documentTypeRepository.existsByCodeAndScopeSystem(code)) {
            throw KnowledgeExceptions.documentTypeCodeAlreadyExists(code.value());
        }
        return DocumentType.createSystem(code, command.name(), command.description(),
                command.category(), classification, command.defaultReviewCycleDays(),
                command.defaultTemplateCode(), command.metadataSchemaJson(), false);
    }

    private DocumentType createOrganization(CreateDocumentTypeCommand command, DocumentTypeCode code,
                                            DocumentClassification classification) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        if (command.organizationId() == null) {
            throw KnowledgeExceptions.documentTypeOrganizationScopeRequiresOrganizationId();
        }
        if (command.workspaceId() != null) {
            throw KnowledgeExceptions.documentTypeInvalidScope(
                    "ORGANIZATION scope must not have workspaceId");
        }
        if (documentTypeRepository.existsByCodeAndOrganizationId(code, command.organizationId())) {
            throw KnowledgeExceptions.documentTypeOrganizationCodeAlreadyExists(
                    code.value(), command.organizationId());
        }
        return DocumentType.createOrganization(code, command.name(), command.description(),
                command.organizationId(), command.category(), classification,
                command.defaultReviewCycleDays(), command.defaultTemplateCode(),
                command.metadataSchemaJson());
    }

    private DocumentType createWorkspace(CreateDocumentTypeCommand command, DocumentTypeCode code,
                                         DocumentClassification classification) {
        if (command.workspaceId() == null) {
            throw KnowledgeExceptions.documentTypeWorkspaceScopeRequiresWorkspaceId();
        }
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_CREATE);

        Workspace workspace = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));
        UUID organizationId = command.organizationId() != null
                ? command.organizationId()
                : workspace.organizationId();
        if (!organizationId.equals(workspace.organizationId())) {
            throw KnowledgeExceptions.documentTypeInvalidScope(
                    "organizationId does not match workspace organization");
        }
        if (documentTypeRepository.existsByCodeAndWorkspaceId(code, command.workspaceId())) {
            throw KnowledgeExceptions.documentTypeWorkspaceCodeAlreadyExists(
                    code.value(), command.workspaceId());
        }
        return DocumentType.createWorkspace(code, command.name(), command.description(),
                organizationId, command.workspaceId(), command.category(), classification,
                command.defaultReviewCycleDays(), command.defaultTemplateCode(),
                command.metadataSchemaJson());
    }

    private DocumentClassification parseClassification(String raw) {
        if (raw == null || raw.isBlank()) {
            return DocumentClassification.INTERNAL;
        }
        try {
            return KnowledgeEnumParser.parseRequired(
                    DocumentClassification.class, raw,
                    KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_CLASSIFICATION.code(),
                    "defaultClassification");
        } catch (Exception e) {
            throw KnowledgeExceptions.documentTypeInvalidClassification(raw);
        }
    }
}
