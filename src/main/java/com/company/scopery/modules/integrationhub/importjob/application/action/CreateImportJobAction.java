package com.company.scopery.modules.integrationhub.importjob.application.action;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportJobResponse;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJob;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJobRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateImportJobAction {
    private final ImportJobRepository jobs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateImportJobAction(ImportJobRepository jobs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.jobs = jobs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ImportJobResponse execute(UUID workspaceId, String jobMode, String sourceFormat, String targetObjectType) {
        auth.requireManage(workspaceId);
        var job = jobs.save(ImportJob.create(workspaceId, jobMode, sourceFormat, targetObjectType));
        activity.logSuccess("INTEGRATION_IMPORT_JOB", job.id(), "INTEGRATION_IMPORT_JOB_CREATED", "Import job created");
        return ImportJobResponse.from(job);
    }
}
