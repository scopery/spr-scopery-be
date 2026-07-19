package com.company.scopery.modules.integrationhub.connection.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheck;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheckRepository;
import com.company.scopery.modules.integrationhub.connection.infrastructure.mapper.ConnectionHealthCheckPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaConnectionHealthCheckRepository implements ConnectionHealthCheckRepository {
    private final SpringDataConnectionHealthCheckJpaRepository spring;
    private final ConnectionHealthCheckPersistenceMapper mapper;
    public JpaConnectionHealthCheckRepository(SpringDataConnectionHealthCheckJpaRepository spring, ConnectionHealthCheckPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ConnectionHealthCheck save(ConnectionHealthCheck c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public List<ConnectionHealthCheck> findByConnectionId(UUID connectionId){ return spring.findByConnectionIdOrderByCheckedAtDesc(connectionId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ConnectionHealthCheck> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
