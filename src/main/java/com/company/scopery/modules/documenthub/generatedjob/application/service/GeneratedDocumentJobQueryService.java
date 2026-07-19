package com.company.scopery.modules.documenthub.generatedjob.application.service;
import com.company.scopery.modules.documenthub.generatedjob.application.response.GeneratedDocumentJobResponse;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJobRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class GeneratedDocumentJobQueryService {
    private final GeneratedDocumentJobRepository repo;
    private final DocumentHubAuthorizationService authorization;
    public GeneratedDocumentJobQueryService(GeneratedDocumentJobRepository repo, DocumentHubAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<GeneratedDocumentJobResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(GeneratedDocumentJobResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public GeneratedDocumentJobResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(GeneratedDocumentJobResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.jobNotFound(id));
    }
}
