package com.company.scopery.modules.collaboration.commentthread.infrastructure.mapper;
import com.company.scopery.modules.collaboration.commentthread.domain.enums.CommentThreadStatus;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThread;
import com.company.scopery.modules.collaboration.commentthread.infrastructure.persistence.CommentThreadJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CommentThreadPersistenceMapper {
    public CommentThread toDomain(CommentThreadJpaEntity e) {
        return new CommentThread(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getTargetType(), e.getTargetId(), e.getTitle(),
                CommentThreadStatus.valueOf(e.getStatus()), e.isInternalOnly(), e.isClientVisible(), e.getResolvedAt(), e.getResolvedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CommentThreadJpaEntity toJpaEntity(CommentThread d) {
        CommentThreadJpaEntity e = new CommentThreadJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setTargetType(d.targetType()); e.setTargetId(d.targetId());
        e.setTitle(d.title()); e.setStatus(d.status().name()); e.setInternalOnly(d.internalOnly()); e.setClientVisible(d.clientVisible());
        e.setResolvedAt(d.resolvedAt()); e.setResolvedBy(d.resolvedBy()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
