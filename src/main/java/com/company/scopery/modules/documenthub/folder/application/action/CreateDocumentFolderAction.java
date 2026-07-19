package com.company.scopery.modules.documenthub.folder.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.documenthub.folder.application.command.CreateDocumentFolderCommand;
import com.company.scopery.modules.documenthub.folder.application.response.DocumentFolderResponse;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolder;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolderRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDocumentFolderAction {
    private final ProjectRepository projects;
    private final DocumentFolderRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    public CreateDocumentFolderAction(ProjectRepository projects, DocumentFolderRepository repo,
            DocumentHubAuthorizationService authorization, DocumentHubActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DocumentFolderResponse execute(CreateDocumentFolderCommand c) {
        authorization.requireCreate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw DocumentHubExceptions.projectArchived(c.projectId());
        var saved = repo.save(DocumentFolder.create(project.workspaceId(), project.id(), c.parentFolderId(), c.name().trim(), c.description(), c.sortOrder()));
        activityLogger.logSuccess(DocumentHubEntityTypes.FOLDER, saved.id(), DocumentHubActivityActions.FOLDER_CREATED, "Folder created");
        return DocumentFolderResponse.from(saved);
    }
}
