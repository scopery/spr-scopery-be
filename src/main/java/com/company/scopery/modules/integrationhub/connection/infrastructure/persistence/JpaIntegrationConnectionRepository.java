package com.company.scopery.modules.integrationhub.connection.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.connection.domain.model.*;
import com.company.scopery.modules.integrationhub.connection.infrastructure.mapper.IntegrationConnectionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaIntegrationConnectionRepository implements IntegrationConnectionRepository {
    private final SpringDataIntegrationConnectionJpaRepository spring; private final IntegrationConnectionPersistenceMapper mapper;
    public JpaIntegrationConnectionRepository(SpringDataIntegrationConnectionJpaRepository spring, IntegrationConnectionPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public IntegrationConnection save(IntegrationConnection c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<IntegrationConnection> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<IntegrationConnection> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<IntegrationConnection> findByStatus(String status){ return spring.findByStatus(status).stream().map(mapper::toDomain).toList(); }
}
