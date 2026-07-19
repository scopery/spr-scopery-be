package com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRelation;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence.DocumentRelationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentRelationPersistenceMapper {

    public DocumentRelation toDomain(DocumentRelationJpaEntity e) {
        return new DocumentRelation(e.getId(), e.getSourceDocumentId(), e.getTargetDocumentId(),
                e.getRelationType(), e.getBlockId(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentRelationJpaEntity toJpaEntity(DocumentRelation d) {
        DocumentRelationJpaEntity e = new DocumentRelationJpaEntity();
        e.setId(d.id());
        e.setSourceDocumentId(d.sourceDocumentId());
        e.setTargetDocumentId(d.targetDocumentId());
        e.setRelationType(d.relationType());
        e.setBlockId(d.blockId());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
