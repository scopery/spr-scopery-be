package com.company.scopery.modules.traceability.requirementcriteria.infrastructure.mapper;
import com.company.scopery.modules.traceability.requirementcriteria.domain.enums.RequirementCriteriaStatus;
import com.company.scopery.modules.traceability.requirementcriteria.domain.enums.RequirementCriteriaType;
import com.company.scopery.modules.traceability.requirementcriteria.domain.model.RequirementCriteria;
import com.company.scopery.modules.traceability.requirementcriteria.infrastructure.persistence.RequirementCriteriaJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RequirementCriteriaPersistenceMapper {
    public RequirementCriteria toDomain(RequirementCriteriaJpaEntity e) {
        return new RequirementCriteria(e.getId(), e.getRequirementId(), e.getWorkspaceId(), e.getDescription(),
                RequirementCriteriaType.valueOf(e.getAcceptanceType()), RequirementCriteriaStatus.valueOf(e.getStatus()),
                e.getDisplayOrder() == null ? 0 : e.getDisplayOrder(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RequirementCriteriaJpaEntity toJpaEntity(RequirementCriteria d) {
        RequirementCriteriaJpaEntity e = new RequirementCriteriaJpaEntity();
        e.setId(d.id()); e.setRequirementId(d.requirementId()); e.setWorkspaceId(d.workspaceId());
        e.setDescription(d.description()); e.setAcceptanceType(d.acceptanceType().name());
        e.setStatus(d.status().name()); e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
