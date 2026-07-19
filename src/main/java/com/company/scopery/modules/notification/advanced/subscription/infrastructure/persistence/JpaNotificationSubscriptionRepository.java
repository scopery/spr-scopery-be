package com.company.scopery.modules.notification.advanced.subscription.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.*;
import com.company.scopery.modules.notification.advanced.subscription.infrastructure.mapper.NotificationSubscriptionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaNotificationSubscriptionRepository implements NotificationSubscriptionRepository {
    private final SpringDataNotificationSubscriptionJpaRepository springData; private final NotificationSubscriptionPersistenceMapper mapper;
    public JpaNotificationSubscriptionRepository(SpringDataNotificationSubscriptionJpaRepository springData, NotificationSubscriptionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public NotificationSubscription save(NotificationSubscription s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public Optional<NotificationSubscription> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<NotificationSubscription> findActiveByUser(UUID workspaceId, UUID userId) {
        return springData.findByWorkspaceIdAndUserIdAndStatus(workspaceId, userId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
}
