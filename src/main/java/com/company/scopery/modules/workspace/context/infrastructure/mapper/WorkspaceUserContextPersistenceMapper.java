package com.company.scopery.modules.workspace.context.infrastructure.mapper;

import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.infrastructure.persistence.WorkspaceUserContextJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceUserContextPersistenceMapper {

    public WorkspaceUserContext toDomain(WorkspaceUserContextJpaEntity e) {
        return new WorkspaceUserContext(
                e.getUserId(), e.getCurrentWorkspaceId(), e.getLastSwitchedAt(),
                e.getOnboardingCompletedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public WorkspaceUserContextJpaEntity toJpaEntity(WorkspaceUserContext d) {
        WorkspaceUserContextJpaEntity e = new WorkspaceUserContextJpaEntity();
        e.setUserId(d.userId());
        e.setCurrentWorkspaceId(d.currentWorkspaceId());
        e.setLastSwitchedAt(d.lastSwitchedAt());
        e.setOnboardingCompletedAt(d.onboardingCompletedAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
