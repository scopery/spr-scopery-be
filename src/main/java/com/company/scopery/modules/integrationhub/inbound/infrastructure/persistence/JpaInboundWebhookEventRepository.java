package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEvent;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEventRepository;
import com.company.scopery.modules.integrationhub.inbound.infrastructure.mapper.InboundWebhookEventPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaInboundWebhookEventRepository implements InboundWebhookEventRepository {
    private final SpringDataInboundWebhookEventJpaRepository spring;
    private final InboundWebhookEventPersistenceMapper mapper;
    public JpaInboundWebhookEventRepository(SpringDataInboundWebhookEventJpaRepository spring, InboundWebhookEventPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public InboundWebhookEvent save(InboundWebhookEvent e){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<InboundWebhookEvent> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public Optional<InboundWebhookEvent> findByInboundEndpointIdAndExternalEventId(UUID endpointId, String externalEventId){ return spring.findByInboundEndpointIdAndExternalEventId(endpointId, externalEventId).map(mapper::toDomain); }
    @Override public List<InboundWebhookEvent> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
