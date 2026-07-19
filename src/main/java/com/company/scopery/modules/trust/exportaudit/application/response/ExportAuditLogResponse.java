package com.company.scopery.modules.trust.exportaudit.application.response;
import com.company.scopery.modules.trust.exportaudit.domain.model.ExportAuditLog;
import java.util.UUID;
public record ExportAuditLogResponse(UUID id, String exportType, String targetObjectType, String classification, long rowCount, String status, String fileReference) {
    public static ExportAuditLogResponse from(ExportAuditLog l){
        return new ExportAuditLogResponse(l.id(), l.exportType(), l.targetObjectType(), l.classification(), l.rowCount(), l.status(), l.fileReference());
    }
}
