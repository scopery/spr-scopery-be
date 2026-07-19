package com.company.scopery.modules.trust.exportaudit.infrastructure.mapper;
import com.company.scopery.modules.trust.exportaudit.domain.model.ExportAuditLog;
import com.company.scopery.modules.trust.exportaudit.infrastructure.persistence.ExportAuditLogJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExportAuditLogPersistenceMapper {
    public ExportAuditLogJpaEntity toJpa(ExportAuditLog d) {
        ExportAuditLogJpaEntity e = new ExportAuditLogJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setExportType(d.exportType());
        e.setTargetObjectType(d.targetObjectType()); e.setClassification(d.classification()); e.setRowCount(d.rowCount());
        e.setFileReference(d.fileReference()); e.setReason(d.reason()); e.setStatus(d.status());
        e.setCompletedAt(d.completedAt()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ExportAuditLog toDomain(ExportAuditLogJpaEntity e) {
        return new ExportAuditLog(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getExportType(), e.getTargetObjectType(),
                e.getClassification(), e.getRowCount()==null?0:e.getRowCount(), e.getFileReference(), e.getReason(), e.getStatus(),
                e.getCompletedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
}
