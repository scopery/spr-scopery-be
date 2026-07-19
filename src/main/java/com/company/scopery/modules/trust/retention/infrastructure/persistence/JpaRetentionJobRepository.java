package com.company.scopery.modules.trust.retention.infrastructure.persistence;
import com.company.scopery.modules.trust.retention.domain.model.*;
import com.company.scopery.modules.trust.retention.infrastructure.mapper.RetentionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaRetentionJobRepository implements RetentionJobRepository {
    private final SpringDataRetentionJobJpaRepository spring; private final RetentionPersistenceMapper mapper;
    public JpaRetentionJobRepository(SpringDataRetentionJobJpaRepository spring, RetentionPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public RetentionJob save(RetentionJob j){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(j))); }
    @Override public Optional<RetentionJob> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<RetentionJob> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
