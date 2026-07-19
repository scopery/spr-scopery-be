package com.company.scopery.modules.integrationhub.exportjob.application.action;
import com.company.scopery.modules.integrationhub.exportjob.application.response.ExportJobResponse;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJob;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJobRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.trust.exportaudit.domain.model.ExportAuditLog;
import com.company.scopery.modules.trust.exportaudit.domain.model.ExportAuditLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateExportJobAction {
    private final ExportJobRepository exports;
    private final ExportAuditLogRepository auditLogs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateExportJobAction(ExportJobRepository exports, ExportAuditLogRepository auditLogs,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.exports = exports; this.auditLogs = auditLogs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ExportJobResponse execute(UUID workspaceId, String exportFormat, String objectScope, UUID projectId,
            UUID exportProfileId, Long rowCount, String reason) {
        auth.requireManage(workspaceId);
        long rows = rowCount == null ? 0L : rowCount;
        String fileRef = "export://integration/" + UUID.randomUUID();
        var audit = auditLogs.save(ExportAuditLog.record(workspaceId, projectId, "INTEGRATION_EXPORT", objectScope, "INTERNAL",
                rows, fileRef, reason == null ? "Integration export" : reason));
        var job = exports.save(ExportJob.create(workspaceId, projectId, exportFormat, objectScope).complete(rows, fileRef, audit.id()));
        activity.logSuccess("INTEGRATION_EXPORT_JOB", job.id(), "INTEGRATION_EXPORT_JOB_CREATED", "Export job created");
        return ExportJobResponse.from(job);
    }
}
