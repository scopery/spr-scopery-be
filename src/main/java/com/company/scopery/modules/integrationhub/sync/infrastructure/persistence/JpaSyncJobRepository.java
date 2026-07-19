package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJob;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJobRepository;
import com.company.scopery.modules.integrationhub.sync.infrastructure.mapper.SyncJobPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSyncJobRepository implements SyncJobRepository {
    private final SpringDataSyncJobJpaRepository spring;
    private final SyncJobPersistenceMapper mapper;
    public JpaSyncJobRepository(SpringDataSyncJobJpaRepository spring, SyncJobPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SyncJob save(SyncJob j){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(j))); }
    @Override public Optional<SyncJob> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SyncJob> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
