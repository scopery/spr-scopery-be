package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscription;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscriptionRepository;
import com.company.scopery.modules.integrationhub.webhook.infrastructure.mapper.WebhookSubscriptionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaWebhookSubscriptionRepository implements WebhookSubscriptionRepository {
    private final SpringDataWebhookSubscriptionJpaRepository spring;
    private final WebhookSubscriptionPersistenceMapper mapper;
    public JpaWebhookSubscriptionRepository(SpringDataWebhookSubscriptionJpaRepository spring, WebhookSubscriptionPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public WebhookSubscription save(WebhookSubscription s){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public Optional<WebhookSubscription> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<WebhookSubscription> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
