package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.command.UpdateDocumentTypeCommand;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.activity.KnowledgeActivityLogger;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeActivityActions;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeEntityTypes;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final KnowledgeActivityLogger activityLogger;

    public UpdateDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                    CurrentUserAuthorizationService currentUserAuthorizationService,
                                    WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                    IamSystemAuthorizationService systemAuthorizationService,
                                    KnowledgeActivityLogger activityLogger) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentTypeResponse execute(UpdateDocumentTypeCommand command) {
        DocumentType dt = findOrThrow(command.id());
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        requireManageAccess(dt, IamAuthorities.DOCUMENT_TYPE_UPDATE);
        DocumentType saved = documentTypeRepository.save(dt.update(command.name(), command.description()));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.UPDATE_DOCUMENT_TYPE,
                "Document type updated: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }

    private void requireManageAccess(DocumentType dt, com.company.scopery.modules.iam.shared.constant.IamPermissionAction workspaceAuthority) {
        if (dt.isSystem()) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        } else {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(dt.workspaceId(), actorId, workspaceAuthority);
        }
    }

    private DocumentType findOrThrow(UUID id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
    }
}
