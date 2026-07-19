package com.company.scopery.modules.configuration.statusset.infrastructure.mapper;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import com.company.scopery.modules.configuration.statusset.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class StatusSetPersistenceMapper {
    public StatusSet toDomain(StatusSetJpaEntity e) {
        return new StatusSet(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getSetCode(), e.getName(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public StatusSetJpaEntity toJpa(StatusSet d) {
        StatusSetJpaEntity e = new StatusSetJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setSetCode(d.setCode()); e.setName(d.name()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public StatusValue toDomain(StatusValueJpaEntity e) {
        return new StatusValue(e.getId(), e.getStatusSetId(), e.getValueCode(), e.getLabel(), e.getDomainCategory(), e.getSortOrder(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public StatusValueJpaEntity toJpa(StatusValue d) {
        StatusValueJpaEntity e = new StatusValueJpaEntity();
        e.setId(d.id()); e.setStatusSetId(d.statusSetId()); e.setValueCode(d.valueCode()); e.setLabel(d.label()); e.setDomainCategory(d.domainCategory()); e.setSortOrder(d.sortOrder()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
