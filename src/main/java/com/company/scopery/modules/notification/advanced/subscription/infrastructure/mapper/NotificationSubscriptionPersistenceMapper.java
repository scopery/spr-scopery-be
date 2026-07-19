package com.company.scopery.modules.notification.advanced.subscription.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.NotificationSubscription;
import com.company.scopery.modules.notification.advanced.subscription.infrastructure.persistence.NotificationSubscriptionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NotificationSubscriptionPersistenceMapper {
    public NotificationSubscription toDomain(NotificationSubscriptionJpaEntity e) {
        return new NotificationSubscription(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getTargetType(), e.getTargetId(),
                e.getSubscriptionLevel(), e.isAutoSubscribed(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public NotificationSubscriptionJpaEntity toJpaEntity(NotificationSubscription d) {
        NotificationSubscriptionJpaEntity e = new NotificationSubscriptionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId()); e.setTargetType(d.targetType());
        e.setTargetId(d.targetId()); e.setSubscriptionLevel(d.subscriptionLevel()); e.setAutoSubscribed(d.autoSubscribed());
        e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
