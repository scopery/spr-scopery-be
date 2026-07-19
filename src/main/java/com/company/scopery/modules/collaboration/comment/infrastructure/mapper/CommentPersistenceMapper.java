package com.company.scopery.modules.collaboration.comment.infrastructure.mapper;
import com.company.scopery.modules.collaboration.comment.domain.enums.*;
import com.company.scopery.modules.collaboration.comment.domain.model.Comment;
import com.company.scopery.modules.collaboration.comment.infrastructure.persistence.CommentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CommentPersistenceMapper {
    public Comment toDomain(CommentJpaEntity e) {
        return new Comment(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getThreadId(), e.getParentCommentId(),
                CommentAuthorType.valueOf(e.getAuthorType()), e.getAuthorId(), e.getAuthorDisplayNameSnapshot(), e.getBody(),
                CommentStatus.valueOf(e.getStatus()), e.isInternalOnly(), e.isClientVisible(), e.getEditedAt(), e.getEditedBy(),
                e.getDeletedAt(), e.getDeletedBy(), e.getTraceId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CommentJpaEntity toJpaEntity(Comment d) {
        CommentJpaEntity e = new CommentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setThreadId(d.threadId());
        e.setParentCommentId(d.parentCommentId()); e.setAuthorType(d.authorType().name()); e.setAuthorId(d.authorId());
        e.setAuthorDisplayNameSnapshot(d.authorDisplayNameSnapshot()); e.setBody(d.body()); e.setStatus(d.status().name());
        e.setInternalOnly(d.internalOnly()); e.setClientVisible(d.clientVisible()); e.setEditedAt(d.editedAt()); e.setEditedBy(d.editedBy());
        e.setDeletedAt(d.deletedAt()); e.setDeletedBy(d.deletedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
