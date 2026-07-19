package com.company.scopery.modules.documenthub.folder.application.service;
import com.company.scopery.modules.documenthub.folder.application.response.DocumentFolderResponse;
import com.company.scopery.modules.documenthub.folder.domain.model.DocumentFolderRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DocumentFolderQueryService {
    private final DocumentFolderRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public DocumentFolderQueryService(DocumentFolderRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DocumentFolderResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(DocumentFolderResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DocumentFolderResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DocumentFolderResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.folderNotFound(id));
    }
}
