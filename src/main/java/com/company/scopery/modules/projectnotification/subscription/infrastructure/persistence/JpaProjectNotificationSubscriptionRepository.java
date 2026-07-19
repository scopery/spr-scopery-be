package com.company.scopery.modules.projectnotification.subscription.infrastructure.persistence;

import com.company.scopery.modules.projectnotification.subscription.domain.enums.ProjectSubscriptionType;
import com.company.scopery.modules.projectnotification.subscription.domain.enums.SubscriptionStatus;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import com.company.scopery.modules.projectnotification.subscription.infrastructure.mapper.ProjectNotificationSubscriptionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectNotificationSubscriptionRepository implements ProjectNotificationSubscriptionRepository {
    private final SpringDataProjectNotificationSubscriptionJpaRepository spring;
    private final ProjectNotificationSubscriptionPersistenceMapper mapper;

    public JpaProjectNotificationSubscriptionRepository(
            SpringDataProjectNotificationSubscriptionJpaRepository spring,
            ProjectNotificationSubscriptionPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public ProjectNotificationSubscription save(ProjectNotificationSubscription subscription) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(subscription)));
    }

    @Override
    public Optional<ProjectNotificationSubscription> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProjectNotificationSubscription> findByProjectId(UUID projectId) {
        return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectNotificationSubscription> findByProjectIdAndSubscriberUserId(UUID projectId, UUID userId) {
        return spring.findByProjectIdAndSubscriberUserId(projectId, userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ProjectNotificationSubscription> findActiveByProjectIdAndType(UUID projectId, ProjectSubscriptionType type) {
        return spring.findByProjectIdAndSubscriptionTypeAndStatus(projectId, type.name(), SubscriptionStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsActive(UUID projectId, UUID subscriberUserId, ProjectSubscriptionType type) {
        return spring.existsByProjectIdAndSubscriberUserIdAndSubscriptionTypeAndStatus(
                projectId, subscriberUserId, type.name(), SubscriptionStatus.ACTIVE.name());
    }

    @Override
    public List<ProjectNotificationSubscription> findByProjectIdAndStatus(UUID projectId, SubscriptionStatus status) {
        return spring.findByProjectIdAndStatus(projectId, status.name()).stream().map(mapper::toDomain).toList();
    }
}
