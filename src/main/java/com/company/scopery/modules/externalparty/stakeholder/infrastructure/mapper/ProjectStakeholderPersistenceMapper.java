package com.company.scopery.modules.externalparty.stakeholder.infrastructure.mapper;
import com.company.scopery.modules.externalparty.stakeholder.domain.enums.StakeholderStatus; import com.company.scopery.modules.externalparty.stakeholder.domain.model.ProjectStakeholder;
import com.company.scopery.modules.externalparty.stakeholder.infrastructure.persistence.ProjectStakeholderJpaEntity; import org.springframework.stereotype.Component;
@Component
public class ProjectStakeholderPersistenceMapper {
    public ProjectStakeholder toDomain(ProjectStakeholderJpaEntity e) {
        return new ProjectStakeholder(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getContactId(), e.getOrganizationId(),
                e.getInternalUserId(), e.getStakeholderRole(), e.getInfluence(), e.getInterest(), StakeholderStatus.valueOf(e.getStatus()),
                e.isClientFacing(), e.getArchivedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProjectStakeholderJpaEntity toJpaEntity(ProjectStakeholder d) {
        ProjectStakeholderJpaEntity e = new ProjectStakeholderJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setContactId(d.contactId());
        e.setOrganizationId(d.organizationId()); e.setInternalUserId(d.internalUserId()); e.setStakeholderRole(d.stakeholderRole());
        e.setInfluence(d.influence()); e.setInterest(d.interest()); e.setStatus(d.status().name()); e.setClientFacing(d.clientFacing());
        e.setArchivedAt(d.archivedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
