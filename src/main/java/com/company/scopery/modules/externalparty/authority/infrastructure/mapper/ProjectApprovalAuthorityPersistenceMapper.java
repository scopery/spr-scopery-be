package com.company.scopery.modules.externalparty.authority.infrastructure.mapper;
import com.company.scopery.modules.externalparty.authority.domain.model.ProjectApprovalAuthority;
import com.company.scopery.modules.externalparty.authority.infrastructure.persistence.ProjectApprovalAuthorityJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProjectApprovalAuthorityPersistenceMapper {
    public ProjectApprovalAuthority toDomain(ProjectApprovalAuthorityJpaEntity e) {
        return new ProjectApprovalAuthority(e.getId(), e.getProjectId(), e.getStakeholderId(), e.getAuthorityType(), e.getStatus(), e.getNotes(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProjectApprovalAuthorityJpaEntity toJpaEntity(ProjectApprovalAuthority d) {
        ProjectApprovalAuthorityJpaEntity e = new ProjectApprovalAuthorityJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setStakeholderId(d.stakeholderId());
        e.setAuthorityType(d.authorityType()); e.setStatus(d.status()); e.setNotes(d.notes()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
