package com.company.scopery.modules.clientportal.auditlog.application.service;
import com.company.scopery.modules.clientportal.auditlog.application.response.ExternalPortalAuditLogResponse;
import com.company.scopery.modules.clientportal.auditlog.domain.model.ExternalPortalAuditLogRepository;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ExternalPortalAuditLogQueryService {
    private final ExternalPortalAuditLogRepository repo;
    private final ClientPortalAuthorizationService authorization;
    public ExternalPortalAuditLogQueryService(ExternalPortalAuditLogRepository repo, ClientPortalAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<ExternalPortalAuditLogResponse> listByProject(UUID projectId) {
        authorization.requireManage(projectId);
        return repo.findByProjectId(projectId).stream().map(ExternalPortalAuditLogResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public List<ExternalPortalAuditLogResponse> listByAccount(UUID workspaceId, UUID portalAccountId) {
        return repo.findByWorkspaceIdAndPortalAccountId(workspaceId, portalAccountId).stream().map(ExternalPortalAuditLogResponse::from).toList();
    }
}
