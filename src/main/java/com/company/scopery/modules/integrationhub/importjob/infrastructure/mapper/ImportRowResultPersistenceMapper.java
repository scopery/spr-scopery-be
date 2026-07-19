package com.company.scopery.modules.integrationhub.importjob.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResult;
import com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence.ImportRowResultJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ImportRowResultPersistenceMapper {
    public ImportRowResultJpaEntity toJpaEntity(ImportRowResult d) {
        ImportRowResultJpaEntity e = new ImportRowResultJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setImportJobId(d.importJobId());
        e.setRowNumber(d.rowNumber()); e.setRowReference(d.rowReference()); e.setStatus(d.status());
        e.setMessage(d.message()); e.setValidationErrorsJson(d.validationErrorsJson());
        e.setTargetObjectType(d.targetObjectType()); e.setTargetObjectId(d.targetObjectId()); e.setExternalId(d.externalId());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ImportRowResult toDomain(ImportRowResultJpaEntity e) {
        return new ImportRowResult(e.getId(), e.getWorkspaceId(), e.getImportJobId(),
                e.getRowNumber() == null ? 0L : e.getRowNumber(), e.getRowReference(), e.getStatus(),
                e.getMessage(), e.getValidationErrorsJson(),
                e.getTargetObjectType(), e.getTargetObjectId(), e.getExternalId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
