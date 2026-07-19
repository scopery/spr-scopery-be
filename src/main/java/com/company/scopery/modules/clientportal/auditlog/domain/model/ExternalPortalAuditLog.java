package com.company.scopery.modules.clientportal.auditlog.domain.model;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalAuditLog(UUID id, UUID workspaceId, UUID projectId, UUID portalAccountId,
                                     String action, String targetType, UUID targetId, String details,
                                     Instant occurredAt, String ipAddress, Instant createdAt) {
    public static ExternalPortalAuditLog create(UUID workspaceId, UUID projectId, UUID portalAccountId,
                                                String action, String targetType, UUID targetId,
                                                String details, String ipAddress) {
        Instant now = Instant.now();
        return new ExternalPortalAuditLog(UUID.randomUUID(), workspaceId, projectId, portalAccountId,
                action, targetType, targetId, details, now, ipAddress, now);
    }
}
