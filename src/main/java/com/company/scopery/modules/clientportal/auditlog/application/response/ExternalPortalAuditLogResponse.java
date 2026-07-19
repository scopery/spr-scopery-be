package com.company.scopery.modules.clientportal.auditlog.application.response;
import com.company.scopery.modules.clientportal.auditlog.domain.model.ExternalPortalAuditLog;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalAuditLogResponse(UUID id, UUID workspaceId, UUID projectId, UUID portalAccountId,
                                             String action, String targetType, UUID targetId, String details,
                                             Instant occurredAt, String ipAddress, Instant createdAt) {
    public static ExternalPortalAuditLogResponse from(ExternalPortalAuditLog d) {
        return new ExternalPortalAuditLogResponse(d.id(), d.workspaceId(), d.projectId(), d.portalAccountId(),
                d.action(), d.targetType(), d.targetId(), d.details(), d.occurredAt(), d.ipAddress(), d.createdAt());
    }
}
