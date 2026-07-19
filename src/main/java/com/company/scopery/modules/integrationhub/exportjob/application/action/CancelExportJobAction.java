package com.company.scopery.modules.integrationhub.exportjob.application.action;
import com.company.scopery.modules.integrationhub.exportjob.application.response.ExportJobResponse;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJobRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelExportJobAction {
    private final ExportJobRepository exports;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CancelExportJobAction(ExportJobRepository exports, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.exports = exports; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ExportJobResponse execute(UUID workspaceId, UUID exportJobId) {
        auth.requireManage(workspaceId);
        var j = exports.findById(exportJobId).orElseThrow(() -> IntegrationExceptions.exportJobNotFound(exportJobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.exportJobNotFound(exportJobId);
        try {
            var saved = exports.save(j.cancel());
            activity.logSuccess("INTEGRATION_EXPORT_JOB", saved.id(), "INTEGRATION_EXPORT_JOB_CANCELLED", "Export job cancelled");
            return ExportJobResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw IntegrationExceptions.exportJobInvalidStatus(j.status());
        }
    }
}
