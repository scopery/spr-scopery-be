package com.company.scopery.modules.projectnotification.subscription.infrastructure.mapper;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import com.company.scopery.modules.projectnotification.subscription.infrastructure.persistence.ProjectNotificationSubscriptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectNotificationSubscriptionPersistenceMapper {
    public ProjectNotificationSubscription toDomain(ProjectNotificationSubscriptionJpaEntity e) {
        return new ProjectNotificationSubscription(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getSubscriberUserId(), e.getWorkspaceMemberId(),
                ProjectSubscriptionType.valueOf(e.getSubscriptionType()), SubscriptionStatus.valueOf(e.getStatus()),
                e.isMandatory(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectNotificationSubscriptionJpaEntity toJpaEntity(ProjectNotificationSubscription d) {
        ProjectNotificationSubscriptionJpaEntity e = new ProjectNotificationSubscriptionJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setSubscriberUserId(d.subscriberUserId());
        e.setWorkspaceMemberId(d.workspaceMemberId());
        e.setSubscriptionType(d.subscriptionType().name());
        e.setStatus(d.status().name());
        e.setMandatory(d.mandatory());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
