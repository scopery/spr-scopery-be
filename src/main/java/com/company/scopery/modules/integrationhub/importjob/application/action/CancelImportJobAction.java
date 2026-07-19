package com.company.scopery.modules.integrationhub.importjob.application.action;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportJobResponse;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJobRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelImportJobAction {
    private final ImportJobRepository jobs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CancelImportJobAction(ImportJobRepository jobs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.jobs = jobs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ImportJobResponse execute(UUID workspaceId, UUID jobId) {
        auth.requireManage(workspaceId);
        var j = jobs.findById(jobId).orElseThrow(() -> IntegrationExceptions.importJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.importJobNotFound(jobId);
        try {
            var saved = jobs.save(j.cancel());
            activity.logSuccess("INTEGRATION_IMPORT_JOB", saved.id(), "INTEGRATION_IMPORT_JOB_CANCELLED", "Import job cancelled");
            return ImportJobResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw IntegrationExceptions.importJobInvalidStatus(j.status());
        }
    }
}
