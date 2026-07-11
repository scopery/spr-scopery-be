package com.company.scopery.modules.knowledge.documenttype.infrastructure.mapper;

import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence.DocumentTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypePersistenceMapper {

    public DocumentType toDomain(DocumentTypeJpaEntity entity) {
        return new DocumentType(
                entity.getId(),
                DocumentTypeCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                DocumentTypeScope.valueOf(entity.getDocumentScope()),
                DocumentTypeStatus.valueOf(entity.getStatus()),
                entity.getWorkspaceId(),
                entity.getDeletedBy(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public DocumentTypeJpaEntity toJpaEntity(DocumentType domain) {
        DocumentTypeJpaEntity entity = new DocumentTypeJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setDocumentScope(domain.documentScope().name());
        entity.setStatus(domain.status().name());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setDeletedAt(domain.deletedAt());
        entity.setDeletedBy(domain.deletedBy());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
