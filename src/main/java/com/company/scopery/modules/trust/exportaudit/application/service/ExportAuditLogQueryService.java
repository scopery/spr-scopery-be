package com.company.scopery.modules.trust.exportaudit.application.service;
import com.company.scopery.modules.trust.exportaudit.application.response.ExportAuditLogResponse;
import com.company.scopery.modules.trust.exportaudit.domain.model.ExportAuditLogRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExportAuditLogQueryService {
    private final ExportAuditLogRepository repo;
    private final TrustAuthorizationService auth;
    public ExportAuditLogQueryService(ExportAuditLogRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ExportAuditLogResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ExportAuditLogResponse::from).toList();
    }
}
