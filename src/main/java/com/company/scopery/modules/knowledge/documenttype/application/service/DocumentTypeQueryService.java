package com.company.scopery.modules.knowledge.documenttype.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.knowledge.documenttype.application.query.SearchDocumentTypeQuery;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentTypeRepository;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeSortFields;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeErrorCatalog;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.util.KnowledgeEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentTypeQueryService {

    private final DocumentTypeRepository documentTypeRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;

    public DocumentTypeQueryService(DocumentTypeRepository documentTypeRepository,
                                     CurrentUserAuthorizationService currentUserAuthorizationService,
                                     WorkspaceIamIntegrationService workspaceIamIntegrationService) {
        this.documentTypeRepository = documentTypeRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
    }

    @Transactional(readOnly = true)
    public DocumentTypeResponse getDocumentType(UUID id) {
        DocumentType documentType = findOrThrow(id);
        if (documentType.documentScope() == DocumentTypeScope.WORKSPACE) {
            requireWorkspaceView(documentType.workspaceId());
        }
        return DocumentTypeResponse.from(documentType);
    }

    @Transactional(readOnly = true)
    public PageResult<DocumentTypeResponse> searchDocumentTypes(SearchDocumentTypeQuery query) {
        DocumentTypeScope scope = KnowledgeEnumParser.parseOptional(
                DocumentTypeScope.class, query.documentScope(),
                KnowledgeErrorCatalog.INVALID_DOCUMENT_TYPE_SCOPE.code(), "documentScope");
        DocumentTypeStatus status = KnowledgeEnumParser.parseOptional(
                DocumentTypeStatus.class, query.status(),
                KnowledgeErrorCatalog.INVALID_DOCUMENT_TYPE_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), KnowledgeSortFields.CREATED_AT, false);

        UUID workspaceId = query.workspaceId();
        if (workspaceId != null) {
            requireWorkspaceView(workspaceId);
        } else {
            // Without a workspace context, only the shared system catalog is browsable —
            // otherwise this would enumerate every workspace's document types at once.
            scope = DocumentTypeScope.SYSTEM;
        }

        return documentTypeRepository.findAll(query.keyword(), workspaceId, scope,
                        status, query.includeDeleted(), pageQuery)
                .map(DocumentTypeResponse::from);
    }

    private void requireWorkspaceView(UUID workspaceId) {
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                workspaceId, actorId, IamAuthorities.DOCUMENT_TYPE_VIEW);
    }

    private DocumentType findOrThrow(UUID id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> KnowledgeExceptions.documentTypeNotFound(id));
    }
}
