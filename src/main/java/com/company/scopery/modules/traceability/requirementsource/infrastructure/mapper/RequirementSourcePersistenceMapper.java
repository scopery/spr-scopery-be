package com.company.scopery.modules.traceability.requirementsource.infrastructure.mapper;
import com.company.scopery.modules.traceability.requirementsource.domain.enums.RequirementSourceStatus;
import com.company.scopery.modules.traceability.requirementsource.domain.model.RequirementSource;
import com.company.scopery.modules.traceability.requirementsource.infrastructure.persistence.RequirementSourceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RequirementSourcePersistenceMapper {
    public RequirementSource toDomain(RequirementSourceJpaEntity e) {
        return new RequirementSource(e.getId(), e.getRequirementId(), e.getWorkspaceId(), e.getSourceType(),
                e.getSourceReference(), e.getDescription(), RequirementSourceStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public RequirementSourceJpaEntity toJpaEntity(RequirementSource d) {
        RequirementSourceJpaEntity e = new RequirementSourceJpaEntity();
        e.setId(d.id()); e.setRequirementId(d.requirementId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceType(d.sourceType()); e.setSourceReference(d.sourceReference()); e.setDescription(d.description());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
