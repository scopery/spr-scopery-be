package com.company.scopery.modules.projectnotification.tasksubscription.infrastructure.persistence;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.enums.TaskSubscriptionType;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import com.company.scopery.modules.projectnotification.tasksubscription.infrastructure.mapper.TaskNotificationSubscriptionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskNotificationSubscriptionRepository implements TaskNotificationSubscriptionRepository {
    private final SpringDataTaskNotificationSubscriptionJpaRepository spring;
    private final TaskNotificationSubscriptionPersistenceMapper mapper;

    public JpaTaskNotificationSubscriptionRepository(
            SpringDataTaskNotificationSubscriptionJpaRepository spring,
            TaskNotificationSubscriptionPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public TaskNotificationSubscription save(TaskNotificationSubscription subscription) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(subscription)));
    }

    @Override
    public Optional<TaskNotificationSubscription> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TaskNotificationSubscription> findByTaskId(UUID taskId) {
        return spring.findByTaskId(taskId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskNotificationSubscription> findByTaskIdAndSubscriberUserId(UUID taskId, UUID userId) {
        return spring.findByTaskIdAndSubscriberUserId(taskId, userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TaskNotificationSubscription> findActiveByTaskId(UUID taskId) {
        return spring.findByTaskIdAndStatus(taskId, SubscriptionStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsActive(UUID taskId, UUID subscriberUserId, TaskSubscriptionType type) {
        return spring.existsByTaskIdAndSubscriberUserIdAndSubscriptionTypeAndStatus(
                taskId, subscriberUserId, type.name(), SubscriptionStatus.ACTIVE.name());
    }
}
