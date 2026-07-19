package com.company.scopery.modules.integrationhub.exportprofile.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfile;
import com.company.scopery.modules.integrationhub.exportprofile.infrastructure.persistence.ExportProfileJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExportProfilePersistenceMapper {
    public ExportProfileJpaEntity toJpaEntity(ExportProfile d) {
        ExportProfileJpaEntity e = new ExportProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setProfileCode(d.profileCode()); e.setName(d.name());
        e.setExportFormat(d.exportFormat()); e.setTargetDestination(d.targetDestination()); e.setObjectScope(d.objectScope());
        e.setColumnsJson(d.columnsJson()); e.setFiltersJson(d.filtersJson()); e.setMaskingPolicy(d.maskingPolicy());
        e.setStatus(d.status()); e.setArchivedAt(d.archivedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ExportProfile toDomain(ExportProfileJpaEntity e) {
        return new ExportProfile(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getProfileCode(), e.getName(),
                e.getExportFormat(), e.getTargetDestination(), e.getObjectScope(),
                e.getColumnsJson(), e.getFiltersJson(), e.getMaskingPolicy(),
                e.getStatus(), e.getArchivedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
