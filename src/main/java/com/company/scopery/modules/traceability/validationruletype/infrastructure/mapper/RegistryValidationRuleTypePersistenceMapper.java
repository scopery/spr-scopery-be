package com.company.scopery.modules.traceability.validationruletype.infrastructure.mapper;

import com.company.scopery.modules.traceability.validationruletype.domain.enums.ValidationRuleTypeStatus;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence.RegistryValidationRuleTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryValidationRuleTypePersistenceMapper {

    public RegistryValidationRuleType toDomain(RegistryValidationRuleTypeJpaEntity e) {
        return new RegistryValidationRuleType(
                e.getId(),
                e.getWorkspaceId(),
                e.getCode(),
                e.getName(),
                e.getCategory(),
                e.getParamSchemaJson(),
                e.getDefaultMessage(),
                e.getDescription(),
                e.isSystem(),
                ValidationRuleTypeStatus.valueOf(e.getStatus()),
                e.getDisplayOrder(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public RegistryValidationRuleTypeJpaEntity toJpaEntity(RegistryValidationRuleType d) {
        RegistryValidationRuleTypeJpaEntity e = new RegistryValidationRuleTypeJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setCode(d.code());
        e.setName(d.name());
        e.setCategory(d.category());
        e.setParamSchemaJson(d.paramSchemaJson());
        e.setDefaultMessage(d.defaultMessage());
        e.setDescription(d.description());
        e.setSystem(d.isSystem());
        e.setStatus(d.status().name());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
