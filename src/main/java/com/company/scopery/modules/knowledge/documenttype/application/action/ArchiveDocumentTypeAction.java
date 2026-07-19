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
public class ArchiveDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final KnowledgeActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final DocumentTypePlatformPublisher platformPublisher;

    public ArchiveDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                     KnowledgeActivityLogger activityLogger,
                                     CurrentUserAuthorizationService currentUserService,
                                     WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                     IamSystemAuthorizationService systemAuthorizationService,
                                     DocumentTypePlatformPublisher platformPublisher) {
        this.documentTypeRepository = documentTypeRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public DocumentTypeResponse execute(UUID id) {
        DocumentType dt = documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
        if (dt.isArchived()) {
            throw KnowledgeExceptions.documentTypeArchivedCannotBeModified(dt.id());
        }
        if (dt.isBuiltIn()) {
            throw KnowledgeExceptions.documentTypeBuiltInCannotDelete(dt.code().value());
        }

        UUID actorId = currentUserService.resolveCurrentUser().id();
        if (dt.isSystem() || dt.isOrganization()) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        } else {
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    dt.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_ARCHIVE);
        }

        DocumentType saved = documentTypeRepository.save(dt.archive(actorId));
        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.ARCHIVE_DOCUMENT_TYPE,
                "Document type archived: " + saved.code().value());
        platformPublisher.enqueue(saved, "DOCUMENT_TYPE_ARCHIVED");
        platformPublisher.auditArchived(actorId, saved);
        return DocumentTypeResponse.from(saved);
    }
}
