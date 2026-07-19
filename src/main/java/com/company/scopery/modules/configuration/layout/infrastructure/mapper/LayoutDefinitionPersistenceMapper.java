package com.company.scopery.modules.configuration.layout.infrastructure.mapper;
import com.company.scopery.modules.configuration.layout.domain.model.LayoutDefinition;
import com.company.scopery.modules.configuration.layout.infrastructure.persistence.LayoutDefinitionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class LayoutDefinitionPersistenceMapper {
    public LayoutDefinition toDomain(LayoutDefinitionJpaEntity e) {
        return new LayoutDefinition(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getLayoutType(), e.getName(), e.getLayoutJson(),
                e.getStatus(), e.isCurrentFlag(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public LayoutDefinitionJpaEntity toJpa(LayoutDefinition d) {
        LayoutDefinitionJpaEntity e = new LayoutDefinitionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setLayoutType(d.layoutType());
        e.setName(d.name()); e.setLayoutJson(d.layoutJson()); e.setStatus(d.status()); e.setCurrentFlag(d.currentFlag()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
