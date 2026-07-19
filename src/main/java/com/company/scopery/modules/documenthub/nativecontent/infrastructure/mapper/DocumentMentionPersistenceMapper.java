package com.company.scopery.modules.documenthub.nativecontent.infrastructure.mapper;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentMention;
import com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence.DocumentMentionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentMentionPersistenceMapper {

    public DocumentMention toDomain(DocumentMentionJpaEntity e) {
        return new DocumentMention(e.getId(), e.getDocumentId(), e.getWorkspaceId(), e.getProjectId(),
                e.getBlockId(), e.getMentionType(), e.getMentionedResourceType(),
                e.getMentionedResourceId(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentMentionJpaEntity toJpaEntity(DocumentMention d) {
        DocumentMentionJpaEntity e = new DocumentMentionJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setBlockId(d.blockId());
        e.setMentionType(d.mentionType());
        e.setMentionedResourceType(d.mentionedResourceType());
        e.setMentionedResourceId(d.mentionedResourceId());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
