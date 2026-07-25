package com.company.scopery.modules.documenthub.document.application.action;

import com.company.scopery.modules.documenthub.document.application.command.UpdateDocumentCommand;
import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateDocumentAction {
    private final DocumentRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public UpdateDocumentAction(
            DocumentRepository repo,
            DocumentHubAuthorizationService authorization,
            DocumentHubActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DocumentResponse execute(UpdateDocumentCommand c) {
        authorization.requireUpdate(c.projectId());
        var doc = repo.findByIdAndProjectId(c.documentId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));
        if (doc.isArchived()) {
            throw DocumentHubExceptions.documentArchivedForEdit(c.documentId());
        }
        if (doc.locked()) {
            throw DocumentHubExceptions.documentLockedForEdit(c.documentId());
        }
        String title = c.title() == null ? "" : c.title().trim();
        if (title.isEmpty()) {
            throw DocumentHubExceptions.titleRequired();
        }
        if (title.equals(doc.title())) {
            return DocumentResponse.from(doc);
        }
        var saved = repo.save(doc.withTitle(title));
        activityLogger.logSuccess(
                DocumentHubEntityTypes.DOCUMENT,
                saved.id(),
                DocumentHubActivityActions.DOCUMENT_UPDATED,
                "Document title updated");
        return DocumentResponse.from(saved);
    }
}
