package com.company.scopery.modules.integrationhub.importjob.application.action;
import com.company.scopery.modules.integrationhub.importjob.application.response.ImportJobResponse;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJobRepository;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResult;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResultRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ExecuteImportJobAction {
    private final ImportJobRepository jobs;
    private final ImportRowResultRepository rows;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public ExecuteImportJobAction(ImportJobRepository jobs, ImportRowResultRepository rows,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.jobs = jobs; this.rows = rows; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ImportJobResponse execute(UUID workspaceId, UUID jobId, long createdRows) {
        auth.requireManage(workspaceId);
        var j = jobs.findById(jobId).orElseThrow(() -> IntegrationExceptions.importJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.importJobNotFound(jobId);
        if (!"DRY_RUN_COMPLETED".equals(j.status()) && !"VALIDATED".equals(j.status())) {
            throw IntegrationExceptions.dryRunRequired();
        }
        var saved = jobs.save(j.execute(createdRows));
        for (long i = 1; i <= createdRows; i++) {
            rows.save(ImportRowResult.created(workspaceId, jobId, i, UUID.randomUUID(), saved.targetObjectType()));
        }
        activity.logSuccess("INTEGRATION_IMPORT_JOB", saved.id(), "INTEGRATION_IMPORT_JOB_EXECUTED",
                "Import job executed with " + createdRows + " rows created");
        return ImportJobResponse.from(saved);
    }
}
