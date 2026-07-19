package com.company.scopery.modules.configuration.validation.infrastructure.mapper;
import com.company.scopery.modules.configuration.validation.domain.model.CustomFieldValidationRule;
import com.company.scopery.modules.configuration.validation.infrastructure.persistence.CustomFieldValidationRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CustomFieldValidationRulePersistenceMapper {
    public CustomFieldValidationRule toDomain(CustomFieldValidationRuleJpaEntity e) {
        return new CustomFieldValidationRule(e.getId(), e.getWorkspaceId(), e.getCustomFieldDefinitionId(), e.getRuleType(), e.getRuleConfigJson(),
                e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFieldValidationRuleJpaEntity toJpa(CustomFieldValidationRule d) {
        CustomFieldValidationRuleJpaEntity e = new CustomFieldValidationRuleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCustomFieldDefinitionId(d.customFieldDefinitionId());
        e.setRuleType(d.ruleType()); e.setRuleConfigJson(d.ruleConfigJson()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
