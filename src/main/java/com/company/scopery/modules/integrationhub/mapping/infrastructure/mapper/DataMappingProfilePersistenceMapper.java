package com.company.scopery.modules.integrationhub.mapping.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfile;
import com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence.DataMappingProfileJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DataMappingProfilePersistenceMapper {
    public DataMappingProfileJpaEntity toJpaEntity(DataMappingProfile d) {
        DataMappingProfileJpaEntity e = new DataMappingProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setMappingCode(d.mappingCode()); e.setName(d.name()); e.setTargetObjectType(d.targetObjectType());
        e.setSourceFormat(d.sourceFormat()); e.setMappingJson(d.mappingJson()); e.setValidationRulesJson(d.validationRulesJson());
        e.setStatus(d.status()); e.setArchivedAt(d.archivedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public DataMappingProfile toDomain(DataMappingProfileJpaEntity e) {
        return new DataMappingProfile(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getMappingCode(), e.getName(),
                e.getTargetObjectType(), e.getSourceFormat(), e.getMappingJson(), e.getValidationRulesJson(),
                e.getStatus(), e.getArchivedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
