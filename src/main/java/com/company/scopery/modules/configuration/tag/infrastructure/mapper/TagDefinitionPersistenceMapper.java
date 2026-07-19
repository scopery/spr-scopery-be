package com.company.scopery.modules.configuration.tag.infrastructure.mapper;
import com.company.scopery.modules.configuration.tag.domain.model.TagDefinition;
import com.company.scopery.modules.configuration.tag.infrastructure.persistence.TagDefinitionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TagDefinitionPersistenceMapper {
    public TagDefinition toDomain(TagDefinitionJpaEntity e) {
        return new TagDefinition(e.getId(), e.getWorkspaceId(), e.getTagCode(), e.getLabel(), e.getColor(), e.getAllowedObjectTypesJson(),
                e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TagDefinitionJpaEntity toJpaEntity(TagDefinition d) {
        TagDefinitionJpaEntity e = new TagDefinitionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setTagCode(d.tagCode()); e.setLabel(d.label());
        e.setColor(d.color()); e.setAllowedObjectTypesJson(d.allowedObjectTypesJson()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
