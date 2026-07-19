package com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndex;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence.DocumentBlockIndexJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentBlockIndexPersistenceMapper {

    public DocumentBlockIndex toDomain(DocumentBlockIndexJpaEntity e) {
        return new DocumentBlockIndex(e.getId(), e.getDocumentId(), e.getBlockId(), e.getBlockType(),
                e.getHeadingLevel(), e.getHeadingText(), e.getPlainText(), e.getOrdinal(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentBlockIndexJpaEntity toJpaEntity(DocumentBlockIndex d) {
        DocumentBlockIndexJpaEntity e = new DocumentBlockIndexJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setBlockId(d.blockId());
        e.setBlockType(d.blockType());
        e.setHeadingLevel(d.headingLevel());
        e.setHeadingText(d.headingText());
        e.setPlainText(d.plainText());
        e.setOrdinal(d.ordinal());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
