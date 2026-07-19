package com.company.scopery.modules.servicesupport.comment.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.comment.domain.model.SupportComment;
import com.company.scopery.modules.servicesupport.comment.infrastructure.persistence.SupportCommentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SupportCommentPersistenceMapper {
    public SupportCommentJpaEntity toJpa(SupportComment d) {
        SupportCommentJpaEntity e = new SupportCommentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setVisibility(d.visibility()); e.setBody(d.body()); e.setAuthorUserId(d.authorUserId());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportComment toDomain(SupportCommentJpaEntity e) {
        return new SupportComment(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getVisibility(),
                e.getBody(), e.getAuthorUserId(), e.getCreatedAt());
    }
}
