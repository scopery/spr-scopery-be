package com.company.scopery.modules.documenthub.template.infrastructure.mapper;

import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplateVariable;
import com.company.scopery.modules.documenthub.template.infrastructure.persistence.DocumentTemplateVariableJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentTemplateVariablePersistenceMapper {

    public DocumentTemplateVariable toDomain(DocumentTemplateVariableJpaEntity e) {
        return new DocumentTemplateVariable(e.getId(), e.getTemplateVersionId(), e.getVariableKey(),
                e.getLabel(), e.getVariableType(), e.isRequired(), e.getDefaultValue(),
                e.isSensitive(), e.getOrdinal(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentTemplateVariableJpaEntity toJpaEntity(DocumentTemplateVariable d) {
        DocumentTemplateVariableJpaEntity e = new DocumentTemplateVariableJpaEntity();
        e.setId(d.id());
        e.setTemplateVersionId(d.templateVersionId());
        e.setVariableKey(d.variableKey());
        e.setLabel(d.label());
        e.setVariableType(d.variableType() != null ? d.variableType() : "TEXT");
        e.setRequired(d.required());
        e.setDefaultValue(d.defaultValue());
        e.setSensitive(d.sensitive());
        e.setOrdinal(d.ordinal());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
