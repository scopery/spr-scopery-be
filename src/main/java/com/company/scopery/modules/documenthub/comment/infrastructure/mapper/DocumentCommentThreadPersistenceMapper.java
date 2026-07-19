package com.company.scopery.modules.documenthub.comment.infrastructure.mapper;

import com.company.scopery.modules.documenthub.comment.domain.enums.CommentThreadStatus;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThread;
import com.company.scopery.modules.documenthub.comment.infrastructure.persistence.DocumentCommentThreadJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentCommentThreadPersistenceMapper {

    public DocumentCommentThread toDomain(DocumentCommentThreadJpaEntity e) {
        return new DocumentCommentThread(
                e.getId(),
                e.getDocumentId(),
                e.getWorkspaceId(),
                e.getProjectId(),
                e.getBlockId(),
                e.getAnchorText(),
                CommentThreadStatus.valueOf(e.getStatus()),
                e.getResolvedBy(),
                e.getResolvedAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public DocumentCommentThreadJpaEntity toJpaEntity(DocumentCommentThread d) {
        DocumentCommentThreadJpaEntity e = new DocumentCommentThreadJpaEntity();
        e.setId(d.id());
        e.setDocumentId(d.documentId());
        e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId());
        e.setBlockId(d.blockId());
        e.setAnchorText(d.anchorText());
        e.setStatus(d.status().name());
        e.setResolvedBy(d.resolvedBy());
        e.setResolvedAt(d.resolvedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
