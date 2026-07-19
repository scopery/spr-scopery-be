package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.modules.documenthub.document.application.command.ApproveDocumentCommand;
import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.domain.enums.DocumentStatus;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApproveDocumentAction {
    private final DocumentRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final DocumentHubActivityLogger activityLogger;

    public ApproveDocumentAction(DocumentRepository repo, DocumentHubAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser, DocumentHubActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentResponse execute(ApproveDocumentCommand c) {
        authorization.requireApprove(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var doc = repo.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));
        if (doc.status() == DocumentStatus.APPROVED || doc.status() == DocumentStatus.ARCHIVED
                || doc.status() == DocumentStatus.DELETED_SOFT) {
            throw DocumentHubExceptions.immutable(c.documentId());
        }
        var saved = repo.save(doc.approve(actor.id()));
        activityLogger.logSuccess(DocumentHubEntityTypes.DOCUMENT, saved.id(),
                DocumentHubActivityActions.DOCUMENT_APPROVED, "Document approved");
        return DocumentResponse.from(saved);
    }
}
