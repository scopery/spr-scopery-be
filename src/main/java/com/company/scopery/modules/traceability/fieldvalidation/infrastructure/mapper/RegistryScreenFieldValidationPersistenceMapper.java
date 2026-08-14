package com.company.scopery.modules.traceability.fieldvalidation.infrastructure.mapper;

import com.company.scopery.modules.traceability.fieldvalidation.domain.enums.FieldValidationStatus;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;
import com.company.scopery.modules.traceability.fieldvalidation.infrastructure.persistence.RegistryScreenFieldValidationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenFieldValidationPersistenceMapper {

    public RegistryScreenFieldValidation toDomain(RegistryScreenFieldValidationJpaEntity e) {
        return new RegistryScreenFieldValidation(
                e.getId(),
                e.getFieldId(),
                e.getModeId(),
                e.getRuleTypeId(),
                e.getWorkspaceId(),
                e.getRuleParamJson(),
                e.getConditionJson(),
                e.getErrorMessage(),
                e.getRemark(),
                e.getDisplayOrder(),
                FieldValidationStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenFieldValidationJpaEntity toJpaEntity(RegistryScreenFieldValidation d) {
        RegistryScreenFieldValidationJpaEntity e = new RegistryScreenFieldValidationJpaEntity();
        e.setId(d.id());
        e.setFieldId(d.fieldId());
        e.setModeId(d.modeId());
        e.setRuleTypeId(d.ruleTypeId());
        e.setWorkspaceId(d.workspaceId());
        e.setRuleParamJson(d.ruleParamJson());
        e.setConditionJson(d.conditionJson());
        e.setErrorMessage(d.errorMessage());
        e.setRemark(d.remark());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
