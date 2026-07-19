package com.company.scopery.modules.trust.exportaudit.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ExportAuditLogRepository {
    ExportAuditLog save(ExportAuditLog log);
    Optional<ExportAuditLog> findById(UUID id);
    List<ExportAuditLog> findByWorkspaceId(UUID workspaceId);
}
