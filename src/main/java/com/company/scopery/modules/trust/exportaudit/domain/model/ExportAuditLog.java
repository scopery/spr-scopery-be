package com.company.scopery.modules.trust.exportaudit.domain.model;
import java.time.Instant; import java.util.UUID;
public record ExportAuditLog(UUID id, UUID workspaceId, UUID projectId, String exportType, String targetObjectType,
        String classification, long rowCount, String fileReference, String reason, String status, Instant completedAt,
        int version, Instant createdAt) {
    public static ExportAuditLog record(UUID workspaceId, UUID projectId, String exportType, String targetType,
                                        String classification, long rows, String fileRef, String reason) {
        Instant now = Instant.now();
        return new ExportAuditLog(UUID.randomUUID(), workspaceId, projectId, exportType, targetType,
                classification == null ? "INTERNAL" : classification, rows, fileRef, reason, "COMPLETED", now, 0, now);
    }
}
