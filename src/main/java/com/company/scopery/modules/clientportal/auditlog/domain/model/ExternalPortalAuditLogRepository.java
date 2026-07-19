package com.company.scopery.modules.clientportal.auditlog.domain.model;
import java.util.List;
import java.util.UUID;
public interface ExternalPortalAuditLogRepository {
    ExternalPortalAuditLog save(ExternalPortalAuditLog entity);
    List<ExternalPortalAuditLog> findByProjectId(UUID projectId);
    List<ExternalPortalAuditLog> findByWorkspaceIdAndPortalAccountId(UUID workspaceId, UUID portalAccountId);
}
