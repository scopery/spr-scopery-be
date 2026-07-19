package com.company.scopery.modules.documenthub.document.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.documenthub.document.application.command.CreateDocumentCommand; import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.document.domain.enums.ContentMode;
import com.company.scopery.modules.documenthub.document.domain.model.*; import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService; import com.company.scopery.modules.documenthub.shared.constant.*;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDocumentAction {
    private final ProjectRepository projects; private final DocumentRepository repo;
    private final DocumentHubAuthorizationService authorization; private final DocumentHubActivityLogger activityLogger;
    public CreateDocumentAction(ProjectRepository projects, DocumentRepository repo, DocumentHubAuthorizationService authorization, DocumentHubActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DocumentResponse execute(CreateDocumentCommand c) {
        authorization.requireCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw DocumentHubExceptions.projectArchived(c.projectId());
        ContentMode contentMode = (c.contentMode() != null) ? ContentMode.valueOf(c.contentMode().toUpperCase()) : ContentMode.FILE;
        var saved = repo.save(Document.create(project.workspaceId(), project.id(), c.folderId(), c.documentTypeCode(), c.code(), c.title().trim(), c.description(), contentMode));
        activityLogger.logSuccess(DocumentHubEntityTypes.DOCUMENT, saved.id(), DocumentHubActivityActions.DOCUMENT_CREATED, "Document created");
        return DocumentResponse.from(saved);
    }
}
