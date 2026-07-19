package com.company.scopery.modules.documenthub.version.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.documenthub.version.application.command.UploadDocumentVersionCommand;
import com.company.scopery.modules.documenthub.version.application.response.DocumentVersionResponse;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersion;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UploadDocumentVersionAction {
    private final ProjectRepository projects;
    private final DocumentVersionRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final DocumentHubActivityLogger activityLogger;
    public UploadDocumentVersionAction(ProjectRepository projects, DocumentVersionRepository repo,
            DocumentHubAuthorizationService authorization, CurrentUserAuthorizationService currentUser, DocumentHubActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public DocumentVersionResponse execute(UploadDocumentVersionCommand c) {
        authorization.requireUpdate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw DocumentHubExceptions.projectArchived(c.projectId());
        int next = repo.nextVersionNumber(c.documentId());
        var saved = repo.save(DocumentVersion.create(c.documentId(), project.id(), project.workspaceId(), next, c.storageKey().trim(),
                c.fileName().trim(), c.contentType(), c.fileSizeBytes(), c.checksum(), c.changeNotes(), currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess(DocumentHubEntityTypes.VERSION, saved.id(), DocumentHubActivityActions.VERSION_UPLOADED, "Version uploaded");
        return DocumentVersionResponse.from(saved);
    }
}
