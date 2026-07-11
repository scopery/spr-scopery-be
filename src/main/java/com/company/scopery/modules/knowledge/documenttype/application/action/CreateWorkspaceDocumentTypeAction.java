package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.command.CreateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateWorkspaceDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final KnowledgeActivityLogger activityLogger;

    public CreateWorkspaceDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                             CurrentUserAuthorizationService currentUserAuthorizationService,
                                             WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                             KnowledgeActivityLogger activityLogger) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentTypeResponse execute(CreateDocumentTypeCommand command) {
        if (command.workspaceId() == null) {
            throw KnowledgeExceptions.documentTypeWorkspaceScopeRequiresWorkspaceId();
        }
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_CREATE);
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
}
