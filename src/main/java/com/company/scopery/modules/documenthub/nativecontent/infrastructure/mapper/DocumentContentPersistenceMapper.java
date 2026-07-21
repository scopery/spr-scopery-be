package com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContent;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence.DocumentContentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentContentPersistenceMapper {

    public DocumentContent toDomain(DocumentContentJpaEntity e) {
        return new DocumentContent(e.getId(), e.getDocumentId(), e.getWorkspaceId(), e.getProjectId(),
                e.getSchemaVersion(), e.getRevisionNo(), e.getAst(), e.getPlainText(),
                e.getWordCount(), e.getCharacterCount(), e.getChecksum(),
                e.getLastSavedAt(), e.getLastSavedBy(), e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentContentJpaEntity toJpaEntity(DocumentContent d) {
        DocumentContentJpaEntity e = new DocumentContentJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setSchemaVersion(d.schemaVersion());
        e.setRevisionNo(d.revisionNo());
        e.setAst(d.ast());
        e.setPlainText(d.plainText());
        e.setWordCount(d.wordCount());
        e.setCharacterCount(d.characterCount());
        e.setChecksum(d.checksum());
        e.setLastSavedAt(d.lastSavedAt());
        e.setLastSavedBy(d.lastSavedBy());
        if (d.createdAt() != null) { e.setCreatedAt(d.createdAt()); e.setVersion(d.version()); }
        return e;
    }
}
