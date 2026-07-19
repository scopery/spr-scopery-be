package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.application.support.DocumentTypePlatformPublisher;
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
public class ActivateDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final KnowledgeActivityLogger activityLogger;
    private final DocumentTypePlatformPublisher platformPublisher;

    public ActivateDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                      IamSystemAuthorizationService systemAuthorizationService,
                                      KnowledgeActivityLogger activityLogger,
                                      DocumentTypePlatformPublisher platformPublisher) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeResponse execute(UUID id) {
        DocumentType dt = documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
        if (dt.isArchived()) {
            throw KnowledgeExceptions.documentTypeArchivedCannotBeModified(dt.id());
        }
        requireManageAccess(dt);
        DocumentType saved = documentTypeRepository.save(dt.activate());
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.ACTIVATE_DOCUMENT_TYPE,
                "Document type activated: " + saved.code().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_ACTIVATED");
        return DocumentTypeResponse.from(saved);
    }

    private void requireManageAccess(DocumentType dt) {
        if (dt.isSystem() || dt.isOrganization()) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        } else {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    dt.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_MANAGE);
        }
    }
}
