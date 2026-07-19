package com.company.scopery.modules.trust.exportaudit.application.action;
import com.company.scopery.modules.trust.exportaudit.application.response.ExportAuditLogResponse;
import com.company.scopery.modules.trust.exportaudit.domain.model.*;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateExportAuditLogAction {
    private final ExportAuditLogRepository repo;
    private final TrustAuthorizationService auth;
    public CreateExportAuditLogAction(ExportAuditLogRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public ExportAuditLogResponse execute(UUID workspaceId, String exportType, String targetObjectType,
            String classification, String reason) {
        auth.requireManage(workspaceId);
        var saved = repo.save(ExportAuditLog.record(workspaceId, null, exportType, targetObjectType,
                classification, 0L, null, reason));
        return ExportAuditLogResponse.from(saved);
    }
}
