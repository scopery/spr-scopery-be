package com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper;

import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence.DocumentRevisionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentRevisionPersistenceMapper {

    public DocumentRevision toDomain(DocumentRevisionJpaEntity e) {
        return new DocumentRevision(e.getId(), e.getDocumentId(), e.getWorkspaceId(), e.getProjectId(),
                e.getRevisionNo(), RevisionType.valueOf(e.getRevisionType()),
                e.getAst(), e.getPlainText(), e.getChecksum(), e.getSchemaVersion(),
                e.getWordCount(), e.getCharacterCount(), e.getCreatedBy(), e.getCreatedAt());
    }

    public DocumentRevisionJpaEntity toJpaEntity(DocumentRevision d) {
        DocumentRevisionJpaEntity e = new DocumentRevisionJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setRevisionNo(d.revisionNo());
        e.setRevisionType(d.revisionType().name());
        e.setAst(d.ast());
        e.setPlainText(d.plainText());
        e.setChecksum(d.checksum());
        e.setSchemaVersion(d.schemaVersion());
        e.setWordCount(d.wordCount());
        e.setCharacterCount(d.characterCount());
        e.setCreatedBy(d.createdBy());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
