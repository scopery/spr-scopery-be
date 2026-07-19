package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpoint;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpointRepository;
import com.company.scopery.modules.integrationhub.inbound.infrastructure.mapper.InboundWebhookEndpointPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaInboundWebhookEndpointRepository implements InboundWebhookEndpointRepository {
    private final SpringDataInboundWebhookEndpointJpaRepository spring;
    private final InboundWebhookEndpointPersistenceMapper mapper;
    public JpaInboundWebhookEndpointRepository(SpringDataInboundWebhookEndpointJpaRepository spring, InboundWebhookEndpointPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public InboundWebhookEndpoint save(InboundWebhookEndpoint e){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<InboundWebhookEndpoint> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public Optional<InboundWebhookEndpoint> findByEndpointCode(String code){ return spring.findByEndpointCode(code).map(mapper::toDomain); }
    @Override public List<InboundWebhookEndpoint> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
