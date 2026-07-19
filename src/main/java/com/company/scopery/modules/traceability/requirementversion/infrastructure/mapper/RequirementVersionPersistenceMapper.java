package com.company.scopery.modules.traceability.requirementversion.infrastructure.mapper;
import com.company.scopery.modules.traceability.requirementversion.domain.model.RequirementVersion;
import com.company.scopery.modules.traceability.requirementversion.infrastructure.persistence.RequirementVersionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RequirementVersionPersistenceMapper {
    public RequirementVersion toDomain(RequirementVersionJpaEntity e) {
        return new RequirementVersion(e.getId(), e.getRequirementId(), e.getWorkspaceId(),
                e.getVersionNumber() == null ? 0 : e.getVersionNumber(), e.getTitle(), e.getDescription(),
                e.getChangeSummary(), e.getCreatedByUserId(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public RequirementVersionJpaEntity toJpaEntity(RequirementVersion d) {
        RequirementVersionJpaEntity e = new RequirementVersionJpaEntity();
        e.setId(d.id()); e.setRequirementId(d.requirementId()); e.setWorkspaceId(d.workspaceId());
        e.setVersionNumber(d.versionNumber()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setChangeSummary(d.changeSummary()); e.setCreatedByUserId(d.createdByUserId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
