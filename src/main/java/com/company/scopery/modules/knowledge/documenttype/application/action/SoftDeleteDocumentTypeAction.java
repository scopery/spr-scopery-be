package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
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
public class SoftDeleteDocumentTypeAction {

    private final DocumentTypeRepository documentTypeRepository;
    private final KnowledgeActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;

    public SoftDeleteDocumentTypeAction(DocumentTypeRepository documentTypeRepository,
                                        KnowledgeActivityLogger activityLogger,
                                        CurrentUserAuthorizationService currentUserService,
                                        WorkspaceIamIntegrationService workspaceIamIntegrationService) {
        this.documentTypeRepository = documentTypeRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
    }

    @Transactional
    public DocumentTypeResponse execute(UUID id) {
        DocumentType dt = documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
        if (dt.isDeleted()) {
            throw KnowledgeExceptions.documentTypeDeletedCannotBeModified(dt.id());
        }
        if (dt.isSystem()) {
            throw KnowledgeExceptions.documentTypeSystemCannotBeDeleted(dt.code().value());
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                dt.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_DELETE);
        DocumentType saved = documentTypeRepository.save(dt.softDelete(actorId));

        activityLogger.logSuccess(KnowledgeEntityTypes.DOCUMENT_TYPE, saved.id(),
                KnowledgeActivityActions.SOFT_DELETE_DOCUMENT_TYPE,
                "Document type soft-deleted: " + saved.code().value());
        return DocumentTypeResponse.from(saved);
    }
}
