package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;

import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttempt;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttemptRepository;
import com.company.scopery.modules.integrationhub.webhook.infrastructure.mapper.WebhookDeliveryAttemptPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWebhookDeliveryAttemptRepository implements WebhookDeliveryAttemptRepository {
    private final SpringDataWebhookDeliveryAttemptJpaRepository spring;
    private final WebhookDeliveryAttemptPersistenceMapper mapper;

    public JpaWebhookDeliveryAttemptRepository(
            SpringDataWebhookDeliveryAttemptJpaRepository spring,
            WebhookDeliveryAttemptPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public WebhookDeliveryAttempt save(WebhookDeliveryAttempt attempt) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(attempt)));
    }

    @Override
    public Optional<WebhookDeliveryAttempt> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WebhookDeliveryAttempt> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
