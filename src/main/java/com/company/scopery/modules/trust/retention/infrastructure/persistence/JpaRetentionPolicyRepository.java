package com.company.scopery.modules.trust.retention.infrastructure.persistence;
import com.company.scopery.modules.trust.retention.domain.model.*;
import com.company.scopery.modules.trust.retention.infrastructure.mapper.RetentionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaRetentionPolicyRepository implements RetentionPolicyRepository {
    private final SpringDataRetentionPolicyJpaRepository spring; private final RetentionPersistenceMapper mapper;
    public JpaRetentionPolicyRepository(SpringDataRetentionPolicyJpaRepository spring, RetentionPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public RetentionPolicy save(RetentionPolicy p){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<RetentionPolicy> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<RetentionPolicy> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
