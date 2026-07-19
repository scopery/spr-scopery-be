package com.company.scopery.modules.documenthub.folder.application.action;

import com.company.scopery.modules.documenthub.folder.application.command.ArchiveDocumentFolderCommand;
import com.company.scopery.modules.documenthub.folder.application.response.DocumentFolderResponse;
import com.company.scopery.modules.documenthub.folder.domain.enums.FolderStatus;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolderRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveDocumentFolderAction {
    private final DocumentFolderRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public ArchiveDocumentFolderAction(DocumentFolderRepository repo, DocumentHubAuthorizationService authorization,
                                       CurrentUserAuthorizationService currentUser) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional
    public DocumentFolderResponse execute(ArchiveDocumentFolderCommand c) {
        authorization.requireUpdate(c.projectId());
        var e = repo.findByIdAndProjectId(c.folderId(), c.projectId())
                .orElseThrow(() -> DocumentHubExceptions.folderNotFound(c.folderId()));
        if (e.status() == FolderStatus.ARCHIVED) {
            throw DocumentHubExceptions.folderAlreadyArchived(c.folderId());
        }
        return DocumentFolderResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
