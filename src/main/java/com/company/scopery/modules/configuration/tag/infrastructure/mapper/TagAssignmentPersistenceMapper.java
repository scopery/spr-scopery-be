package com.company.scopery.modules.configuration.tag.infrastructure.mapper;
import com.company.scopery.modules.configuration.tag.domain.model.TagAssignment;
import com.company.scopery.modules.configuration.tag.infrastructure.persistence.TagAssignmentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TagAssignmentPersistenceMapper {
    public TagAssignment toDomain(TagAssignmentJpaEntity e) {
        return new TagAssignment(e.getId(), e.getWorkspaceId(), e.getTagDefinitionId(), e.getObjectTypeCode(), e.getTargetId(),
                e.getArchivedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TagAssignmentJpaEntity toJpaEntity(TagAssignment d) {
        TagAssignmentJpaEntity e = new TagAssignmentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setTagDefinitionId(d.tagDefinitionId());
        e.setObjectTypeCode(d.objectTypeCode()); e.setTargetId(d.targetId()); e.setArchivedAt(d.archivedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
