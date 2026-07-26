package com.company.scopery.modules.documenthub.documentlink.infrastructure.mapper;

import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkEntityType;
import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkRelationType;
import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLink;
import com.company.scopery.modules.documenthub.documentlink.infrastructure.persistence.DocumentLinkJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentLinkPersistenceMapper {

    public DocumentLink toDomain(DocumentLinkJpaEntity e) {
        return new DocumentLink(
                e.getId(),
                e.getDocumentId(),
                e.getProjectId(),
                DocumentLinkEntityType.fromString(e.getTargetType()),
                e.getTargetId(),
                DocumentLinkRelationType.fromString(e.getLinkType()),
                e.getArchivedAt(),
                e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getCreatedBy()
        );
    }

    public DocumentLinkJpaEntity toJpaEntity(DocumentLink d) {
        DocumentLinkJpaEntity e = new DocumentLinkJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setProjectId(d.projectId());
        e.setTargetType(d.linkedEntityType().name());
        e.setTargetId(d.linkedEntityId());
        e.setLinkType(d.relationType().name());
        e.setArchivedAt(d.archivedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
