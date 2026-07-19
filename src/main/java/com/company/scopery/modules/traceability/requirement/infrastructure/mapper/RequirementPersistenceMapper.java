package com.company.scopery.modules.traceability.requirement.infrastructure.mapper;
import com.company.scopery.modules.traceability.requirement.domain.enums.*; import com.company.scopery.modules.traceability.requirement.domain.model.Requirement;
import com.company.scopery.modules.traceability.requirement.infrastructure.persistence.RequirementJpaEntity; import org.springframework.stereotype.Component;
@Component
public class RequirementPersistenceMapper {
    public Requirement toDomain(RequirementJpaEntity e) {
        return new Requirement(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getApplicationId(), e.getCode(), e.getTitle(), e.getDescription(),
                RequirementType.valueOf(e.getRequirementType()), RequirementPriority.valueOf(e.getPriority()), RequirementStatus.valueOf(e.getStatus()),
                e.getOwnerUserId(), e.getCurrentVersionNumber(), e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RequirementJpaEntity toJpaEntity(Requirement d) {
        RequirementJpaEntity e = new RequirementJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setApplicationId(d.applicationId());
        e.setCode(d.code()); e.setTitle(d.title()); e.setDescription(d.description()); e.setRequirementType(d.requirementType().name());
        e.setPriority(d.priority().name()); e.setStatus(d.status().name()); e.setOwnerUserId(d.ownerUserId());
        e.setCurrentVersionNumber(d.currentVersionNumber()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
