package com.company.scopery.modules.projectnotification.tasksubscription.infrastructure.mapper;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.enums.TaskSubscriptionType;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.infrastructure.persistence.TaskNotificationSubscriptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationSubscriptionPersistenceMapper {
    public TaskNotificationSubscription toDomain(TaskNotificationSubscriptionJpaEntity e) {
        return new TaskNotificationSubscription(
                e.getId(), e.getProjectId(), e.getTaskId(), e.getWorkspaceId(), e.getSubscriberUserId(),
                e.getWorkspaceMemberId(), TaskSubscriptionType.valueOf(e.getSubscriptionType()),
                SubscriptionStatus.valueOf(e.getStatus()), e.isMandatory(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public TaskNotificationSubscriptionJpaEntity toJpaEntity(TaskNotificationSubscription d) {
        TaskNotificationSubscriptionJpaEntity e = new TaskNotificationSubscriptionJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setTaskId(d.taskId());
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
