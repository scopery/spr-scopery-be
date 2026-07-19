package com.company.scopery.modules.documenthub.share.application.service;
import com.company.scopery.modules.documenthub.share.application.response.DocumentShareResponse;
import com.company.scopery.modules.documenthub.share.domain.model.DocumentShareRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DocumentShareQueryService {
    private final DocumentShareRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public DocumentShareQueryService(DocumentShareRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DocumentShareResponse> list(UUID projectId, UUID documentId) {
        authorization.requireView(projectId);
        return repo.findByProjectIdAndDocumentId(projectId, documentId).stream().map(DocumentShareResponse::from).toList();
    }
}
