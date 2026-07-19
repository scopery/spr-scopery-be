package com.company.scopery.modules.documenthub.comment.infrastructure.mapper;

import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;
import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentReply;
import com.company.scopery.modules.documenthub.comment.infrastructure.persistence.DocumentCommentJpaEntity;
import com.company.scopery.modules.documenthub.comment.infrastructure.persistence.DocumentCommentReplyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DocumentCommentPersistenceMapper {

    public DocumentComment toDomain(DocumentCommentJpaEntity e) {
        return new DocumentComment(e.getId(), e.getThreadId(), e.getDocumentId(),
                e.getBody(), e.getDeletedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentCommentJpaEntity toJpaEntity(DocumentComment d) {
        DocumentCommentJpaEntity e = new DocumentCommentJpaEntity();
        e.setId(d.id());
        e.setThreadId(d.threadId());
        e.setDocumentId(d.documentId());
        e.setBody(d.body());
        e.setDeletedAt(d.deletedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }

    public DocumentCommentReply toReplyDomain(DocumentCommentReplyJpaEntity e) {
        return new DocumentCommentReply(e.getId(), e.getCommentId(), e.getThreadId(),
                e.getBody(), e.getDeletedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public DocumentCommentReplyJpaEntity toReplyJpaEntity(DocumentCommentReply d) {
        DocumentCommentReplyJpaEntity e = new DocumentCommentReplyJpaEntity();
        e.setId(d.id());
        e.setCommentId(d.commentId());
        e.setThreadId(d.threadId());
        e.setBody(d.body());
        e.setDeletedAt(d.deletedAt());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
