package com.company.scopery.modules.integrationhub.exportjob.application.service;
import com.company.scopery.modules.integrationhub.exportjob.application.response.ExportJobResponse;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJobRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExportJobQueryService {
    private final ExportJobRepository repo;
    private final IntegrationAuthorizationService auth;
    public ExportJobQueryService(ExportJobRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ExportJobResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ExportJobResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ExportJobResponse getById(UUID workspaceId, UUID jobId) {
        auth.requireView(workspaceId);
        var j = repo.findById(jobId).orElseThrow(() -> IntegrationExceptions.exportJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.exportJobNotFound(jobId);
        return ExportJobResponse.from(j);
    }
}
