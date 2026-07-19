package com.company.scopery.modules.knowledge.documenttypefield.infrastructure.mapper;

import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldDataType;
import com.company.scopery.modules.knowledge.documenttypefield.domain.enums.DocumentTypeFieldStatus;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.documenttypefield.domain.valueobject.DocumentTypeFieldKey;
import com.company.scopery.modules.knowledge.documenttypefield.infrastructure.persistence.DocumentTypeFieldJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeFieldPersistenceMapper {

    public DocumentTypeField toDomain(DocumentTypeFieldJpaEntity entity) {
        return new DocumentTypeField(
                entity.getId(),
                entity.getDocumentTypeId(),
                DocumentTypeFieldKey.of(entity.getFieldKey()),
                entity.getLabel(),
                entity.getDescription(),
                DocumentTypeFieldDataType.valueOf(entity.getDataType()),
                entity.isRequired(),
                entity.isSystemField(),
                entity.getOptionsJson(),
                entity.getValidationJson(),
                entity.getDefaultValueJson(),
                entity.getDisplayOrder(),
                DocumentTypeFieldStatus.valueOf(entity.getStatus()),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public DocumentTypeFieldJpaEntity toJpaEntity(DocumentTypeField domain) {
        DocumentTypeFieldJpaEntity entity = new DocumentTypeFieldJpaEntity();
        entity.setId(domain.id());
        entity.setDocumentTypeId(domain.documentTypeId());
        entity.setFieldKey(domain.fieldKey().value());
        entity.setLabel(domain.label());
        entity.setDescription(domain.description());
        entity.setDataType(domain.dataType().name());
        entity.setRequired(domain.required());
        entity.setSystemField(domain.systemField());
        entity.setOptionsJson(domain.optionsJson());
        entity.setValidationJson(domain.validationJson());
        entity.setDefaultValueJson(domain.defaultValueJson());
        entity.setDisplayOrder(domain.displayOrder());
        entity.setStatus(domain.status().name());
        if (domain.createdAt() != null) { entity.setCreatedAt(domain.createdAt()); entity.setVersion(domain.version()); }
        return entity;
    }
}
