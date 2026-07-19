package com.company.scopery.modules.scope.mapping.infrastructure.mapper;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMapping;
import com.company.scopery.modules.scope.mapping.infrastructure.persistence.ScopeItemWbsMappingJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ScopeItemWbsMappingPersistenceMapper {
    public ScopeItemWbsMapping toDomain(ScopeItemWbsMappingJpaEntity e) {
        return new ScopeItemWbsMapping(e.getId(), e.getScopeItemId(), e.getProjectId(), e.getWbsNodeId(),
                MappingType.valueOf(e.getMappingType()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public ScopeItemWbsMappingJpaEntity toJpaEntity(ScopeItemWbsMapping d) {
        ScopeItemWbsMappingJpaEntity e = new ScopeItemWbsMappingJpaEntity();
        e.setId(d.id()); e.setScopeItemId(d.scopeItemId()); e.setProjectId(d.projectId());
        e.setWbsNodeId(d.wbsNodeId()); e.setMappingType(d.mappingType().name());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
