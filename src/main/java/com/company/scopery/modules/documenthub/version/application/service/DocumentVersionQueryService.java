package com.company.scopery.modules.documenthub.version.application.service;
import com.company.scopery.modules.documenthub.version.application.response.DocumentVersionResponse;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DocumentVersionQueryService {
    private final DocumentVersionRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public DocumentVersionQueryService(DocumentVersionRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DocumentVersionResponse> list(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return repo.findByProjectIdAndDocumentId(projectId, documentId).stream().map(DocumentVersionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DocumentVersionResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DocumentVersionResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.versionNotFound(id));
    }
}
