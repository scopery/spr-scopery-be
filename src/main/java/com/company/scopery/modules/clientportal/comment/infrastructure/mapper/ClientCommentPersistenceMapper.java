package com.company.scopery.modules.clientportal.comment.infrastructure.mapper;
import com.company.scopery.modules.clientportal.comment.domain.enums.ClientCommentStatus;
import com.company.scopery.modules.clientportal.comment.domain.model.ClientComment;
import com.company.scopery.modules.clientportal.comment.infrastructure.persistence.ClientCommentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ClientCommentPersistenceMapper {
    public ClientComment toDomain(ClientCommentJpaEntity e) {
        return new ClientComment(e.getId(), e.getProjectId(), e.getTargetType(), e.getTargetId(), e.getBody(), e.getAuthorPortalAccountId(),
                e.getStatus() != null ? ClientCommentStatus.valueOf(e.getStatus()) : ClientCommentStatus.ACTIVE,
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ClientCommentJpaEntity toJpaEntity(ClientComment d) {
        ClientCommentJpaEntity e = new ClientCommentJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTargetType(d.targetType()); e.setTargetId(d.targetId());
        e.setBody(d.body()); e.setAuthorPortalAccountId(d.authorPortalAccountId()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
