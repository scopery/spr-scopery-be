package com.company.scopery.modules.knowledge.documenttypefield.application.support;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DocumentTypeFieldAccessGuard {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public DocumentTypeFieldAccessGuard(DocumentTypeRepository documentTypeRepository,
                                        CurrentUserAuthorizationService currentUserAuthorizationService,
                                        WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                        IamSystemAuthorizationService systemAuthorizationService) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    public DocumentType requireDocumentType(UUID documentTypeId) {
        return requireDocumentType(documentTypeId, true);
    }

    public DocumentType requireDocumentType(UUID documentTypeId, boolean rejectArchived) {
        DocumentType dt = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(documentTypeId));
        if (rejectArchived && dt.isArchived()) {
            throw KnowledgeExceptions.documentTypeArchivedCannotBeModified(documentTypeId);
        }
        return dt;
    }

    public void requireWrite(DocumentType dt, IamPermissionAction workspaceAuthority) {
        if (dt.isSystem() || dt.isOrganization()) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE.legacyRightCode());
        } else {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    dt.workspaceId(), actorId, workspaceAuthority);
        }
    }

    public void requireView(DocumentType dt) {
        if (dt.isWorkspace()) {
            UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    dt.workspaceId(), actorId, IamAuthorities.DOCUMENT_TYPE_FIELD_VIEW);
        }
    }
}
