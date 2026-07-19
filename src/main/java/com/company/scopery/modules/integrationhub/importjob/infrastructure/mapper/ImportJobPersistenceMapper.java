package com.company.scopery.modules.integrationhub.importjob.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJob;
import com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence.ImportJobJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ImportJobPersistenceMapper {
    public ImportJobJpaEntity toJpa(ImportJob d) {
        ImportJobJpaEntity e = new ImportJobJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setJobMode(d.jobMode()); e.setSourceFormat(d.sourceFormat());
        e.setTargetObjectType(d.targetObjectType()); e.setStatus(d.status()); e.setTotalRows(d.totalRows());
        e.setValidRows(d.validRows()); e.setInvalidRows(d.invalidRows()); e.setCreatedRows(d.createdRows());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ImportJob toDomain(ImportJobJpaEntity e) {
        return new ImportJob(e.getId(), e.getWorkspaceId(), e.getJobMode(), e.getSourceFormat(), e.getTargetObjectType(), e.getStatus(),
                e.getTotalRows()==null?0:e.getTotalRows(), e.getValidRows()==null?0:e.getValidRows(),
                e.getInvalidRows()==null?0:e.getInvalidRows(), e.getCreatedRows()==null?0:e.getCreatedRows(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
