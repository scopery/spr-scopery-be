package com.company.scopery.modules.integrationhub.importjob.application.service;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportJobResponse;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportRowResultResponse;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJobRepository;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResultRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ImportJobQueryService {
    private final ImportJobRepository jobs;
    private final ImportRowResultRepository rows;
    private final IntegrationAuthorizationService auth;
    public ImportJobQueryService(ImportJobRepository jobs, ImportRowResultRepository rows, IntegrationAuthorizationService auth) {
        this.jobs = jobs; this.rows = rows; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ImportJobResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return jobs.findByWorkspaceId(workspaceId).stream().map(ImportJobResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public ImportJobResponse getById(UUID workspaceId, UUID jobId) {
        auth.requireView(workspaceId);
        var j = jobs.findById(jobId).orElseThrow(() -> IntegrationExceptions.importJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.importJobNotFound(jobId);
        return ImportJobResponse.from(j);
    }
    @Transactional(readOnly = true)
    public List<ImportRowResultResponse> listRows(UUID workspaceId, UUID jobId) {
        auth.requireView(workspaceId);
        return rows.findByWorkspaceIdAndImportJobId(workspaceId, jobId).stream().map(ImportRowResultResponse::from).toList();
    }
}
