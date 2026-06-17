package com.company.scopery.modules.workspace.joinrequest.infrastructure.mapper;

import com.company.scopery.modules.workspace.joinrequest.domain.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.WorkspaceJoinRequestStatus;
import com.company.scopery.modules.workspace.joinrequest.infrastructure.persistence.WorkspaceJoinRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceJoinRequestPersistenceMapper {

    public WorkspaceJoinRequest toDomain(WorkspaceJoinRequestJpaEntity e) {
        return new WorkspaceJoinRequest(
                e.getId(), e.getWorkspaceId(), e.getRequestedByUserId(), e.getMessage(),
                WorkspaceJoinRequestStatus.valueOf(e.getStatus()),
                e.getReviewedByUserId(), e.getReviewedAt(), e.getReviewNote(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public WorkspaceJoinRequestJpaEntity toJpaEntity(WorkspaceJoinRequest d) {
        WorkspaceJoinRequestJpaEntity e = new WorkspaceJoinRequestJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setRequestedByUserId(d.requestedByUserId());
        e.setMessage(d.message());
        e.setStatus(d.status().name());
        e.setReviewedByUserId(d.reviewedByUserId());
        e.setReviewedAt(d.reviewedAt());
        e.setReviewNote(d.reviewNote());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
