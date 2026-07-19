package com.company.scopery.modules.integrationhub.exportjob.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJob;
import com.company.scopery.modules.integrationhub.exportjob.infrastructure.persistence.ExportJobJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExportJobPersistenceMapper {
    public ExportJobJpaEntity toJpa(ExportJob d) {
        ExportJobJpaEntity e = new ExportJobJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setExportFormat(d.exportFormat());
        e.setObjectScope(d.objectScope()); e.setStatus(d.status()); e.setRowCount(d.rowCount()); e.setFileReference(d.fileReference());
        e.setExportAuditLogId(d.exportAuditLogId()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ExportJob toDomain(ExportJobJpaEntity e) {
        return new ExportJob(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getExportFormat(), e.getObjectScope(), e.getStatus(),
                e.getRowCount()==null?0:e.getRowCount(), e.getFileReference(), e.getExportAuditLogId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
