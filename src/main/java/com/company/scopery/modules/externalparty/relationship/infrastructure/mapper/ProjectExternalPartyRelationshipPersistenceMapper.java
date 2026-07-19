package com.company.scopery.modules.externalparty.relationship.infrastructure.mapper;
import com.company.scopery.modules.externalparty.relationship.domain.model.ProjectExternalPartyRelationship;
import com.company.scopery.modules.externalparty.relationship.infrastructure.persistence.ProjectExternalPartyRelationshipJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProjectExternalPartyRelationshipPersistenceMapper {
    public ProjectExternalPartyRelationship toDomain(ProjectExternalPartyRelationshipJpaEntity e) {
        return new ProjectExternalPartyRelationship(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getOrganizationId(), e.getRelationshipType(), e.getStatus(), e.getNotes(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProjectExternalPartyRelationshipJpaEntity toJpaEntity(ProjectExternalPartyRelationship d) {
        ProjectExternalPartyRelationshipJpaEntity e = new ProjectExternalPartyRelationshipJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setOrganizationId(d.organizationId());
        e.setRelationshipType(d.relationshipType()); e.setStatus(d.status()); e.setNotes(d.notes()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
