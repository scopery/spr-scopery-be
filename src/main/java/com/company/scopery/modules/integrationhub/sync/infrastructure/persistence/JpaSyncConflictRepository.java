package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflict;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflictRepository;
import com.company.scopery.modules.integrationhub.sync.infrastructure.mapper.SyncConflictPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSyncConflictRepository implements SyncConflictRepository {
    private final SpringDataSyncConflictJpaRepository spring;
    private final SyncConflictPersistenceMapper mapper;
    public JpaSyncConflictRepository(SpringDataSyncConflictJpaRepository spring, SyncConflictPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SyncConflict save(SyncConflict c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<SyncConflict> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SyncConflict> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
