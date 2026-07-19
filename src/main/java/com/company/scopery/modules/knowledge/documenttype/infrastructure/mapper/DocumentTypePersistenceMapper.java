package com.company.scopery.modules.knowledge.documenttype.infrastructure.mapper;

import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeScope;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentTypeStatus;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.documenttype.domain.valueobject.DocumentTypeCode;
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
                entity.getOrganizationId(),
                entity.getWorkspaceId(),
                entity.getCategory(),
                entity.getDefaultClassification() != null
                        ? DocumentClassification.valueOf(entity.getDefaultClassification())
                        : DocumentClassification.INTERNAL,
                entity.getDefaultReviewCycleDays(),
                entity.getDefaultTemplateCode(),
                entity.getMetadataSchemaJson(),
                entity.isBuiltIn(),
                entity.getArchivedBy(),
                entity.getArchivedAt(),
                entity.getDeletedBy(),
                entity.getDeletedAt(),
                entity.getVersion(),
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
        entity.setOrganizationId(domain.organizationId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCategory(domain.category());
        entity.setDefaultClassification(
                domain.defaultClassification() != null ? domain.defaultClassification().name() : null);
        entity.setDefaultReviewCycleDays(domain.defaultReviewCycleDays());
        entity.setDefaultTemplateCode(domain.defaultTemplateCode());
        entity.setMetadataSchemaJson(domain.metadataSchemaJson());
        entity.setBuiltIn(domain.builtIn());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        entity.setDeletedAt(domain.deletedAt());
        entity.setDeletedBy(domain.deletedBy());
        if (domain.createdAt() != null) { entity.setCreatedAt(domain.createdAt()); entity.setVersion(domain.version()); }
        return entity;
    }
}
