package com.company.scopery.modules.integrationhub.sync.application.service;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncConflictResponse;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncJobResponse;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncRunResponse;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflictRepository;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJobRepository;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class SyncJobQueryService {
    private final SyncJobRepository jobs;
    private final SyncRunRepository runs;
    private final SyncConflictRepository conflicts;
    private final IntegrationAuthorizationService auth;
    public SyncJobQueryService(SyncJobRepository jobs, SyncRunRepository runs, SyncConflictRepository conflicts,
            IntegrationAuthorizationService auth) {
        this.jobs = jobs; this.runs = runs; this.conflicts = conflicts; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<SyncJobResponse> listJobs(UUID workspaceId) {
        auth.requireView(workspaceId);
        return jobs.findByWorkspaceId(workspaceId).stream().map(SyncJobResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public SyncJobResponse getJobById(UUID workspaceId, UUID jobId) {
        auth.requireView(workspaceId);
        var j = jobs.findById(jobId).orElseThrow(() -> IntegrationExceptions.syncJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.syncJobNotFound(jobId);
        return SyncJobResponse.from(j);
    }
    @Transactional(readOnly = true)
    public List<SyncRunResponse> listRuns(UUID workspaceId) {
        auth.requireView(workspaceId);
        return runs.findByWorkspaceId(workspaceId).stream().map(SyncRunResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public SyncRunResponse getRunById(UUID workspaceId, UUID runId) {
        auth.requireView(workspaceId);
        var r = runs.findById(runId).orElseThrow(() -> IntegrationExceptions.syncRunNotFound(runId));
        if (!workspaceId.equals(r.workspaceId())) throw IntegrationExceptions.syncRunNotFound(runId);
        return SyncRunResponse.from(r);
    }
    @Transactional(readOnly = true)
    public List<SyncConflictResponse> listConflicts(UUID workspaceId) {
        auth.requireView(workspaceId);
        return conflicts.findByWorkspaceId(workspaceId).stream().map(SyncConflictResponse::from).toList();
    }
}
