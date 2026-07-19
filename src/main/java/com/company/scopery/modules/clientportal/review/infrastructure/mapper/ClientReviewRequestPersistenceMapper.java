package com.company.scopery.modules.clientportal.review.infrastructure.mapper;
import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewStatus; import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewRequest;
import com.company.scopery.modules.clientportal.review.infrastructure.persistence.ClientReviewRequestJpaEntity; import org.springframework.stereotype.Component;
@Component
public class ClientReviewRequestPersistenceMapper {
    public ClientReviewRequest toDomain(ClientReviewRequestJpaEntity e) {
        return new ClientReviewRequest(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getTargetType(), e.getTargetId(), e.getTitle(),
                ClientReviewStatus.valueOf(e.getStatus()), e.getDueAt(), e.getRequestedBy(), e.getAssignedPortalAccountId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ClientReviewRequestJpaEntity toJpaEntity(ClientReviewRequest d) {
        ClientReviewRequestJpaEntity e = new ClientReviewRequestJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setTargetType(d.targetType());
        e.setTargetId(d.targetId()); e.setTitle(d.title()); e.setStatus(d.status().name()); e.setDueAt(d.dueAt());
        e.setRequestedBy(d.requestedBy()); e.setAssignedPortalAccountId(d.assignedPortalAccountId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
