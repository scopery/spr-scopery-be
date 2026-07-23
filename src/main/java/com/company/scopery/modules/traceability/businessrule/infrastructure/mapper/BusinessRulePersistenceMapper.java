package com.company.scopery.modules.traceability.businessrule.infrastructure.mapper;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleSeverity;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleStatus;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;
import com.company.scopery.modules.traceability.businessrule.infrastructure.persistence.BusinessRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class BusinessRulePersistenceMapper {
    public BusinessRule toDomain(BusinessRuleJpaEntity e) {
        return new BusinessRule(
                e.getId(),
                e.getFunctionalItemId(),
                e.getProjectId(),
                e.getCode(),
                e.getTitle(),
                e.getDescription(),
                e.getSeverity() != null ? BusinessRuleSeverity.valueOf(e.getSeverity()) : null,
                e.getStatus() != null ? BusinessRuleStatus.valueOf(e.getStatus()) : BusinessRuleStatus.DRAFT,
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
    public BusinessRuleJpaEntity toJpaEntity(BusinessRule d) {
        BusinessRuleJpaEntity e = new BusinessRuleJpaEntity();
        e.setId(d.id());
        e.setFunctionalItemId(d.functionalItemId());
        e.setProjectId(d.projectId());
        e.setCode(d.code());
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setSeverity(d.severity() != null ? d.severity().name() : null);
        e.setStatus(d.status() != null ? d.status().name() : null);
        // New: leave version/createdAt null → persist. Update: stamp both for optimistic lock.
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
