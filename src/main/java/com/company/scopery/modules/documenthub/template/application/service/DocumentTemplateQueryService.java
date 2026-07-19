package com.company.scopery.modules.documenthub.template.application.service;
import com.company.scopery.modules.documenthub.template.application.response.DocumentTemplateResponse;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DocumentTemplateQueryService {
    private final DocumentTemplateRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public DocumentTemplateQueryService(DocumentTemplateRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DocumentTemplateResponse> list(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(DocumentTemplateResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DocumentTemplateResponse get(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(id, workspaceId).map(DocumentTemplateResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.templateNotFound(id));
    }
}
