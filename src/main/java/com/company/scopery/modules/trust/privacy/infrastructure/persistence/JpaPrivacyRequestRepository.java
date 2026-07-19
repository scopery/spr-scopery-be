package com.company.scopery.modules.trust.privacy.infrastructure.persistence;
import com.company.scopery.modules.trust.privacy.domain.model.*;
import com.company.scopery.modules.trust.privacy.infrastructure.mapper.PrivacyRequestPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaPrivacyRequestRepository implements PrivacyRequestRepository {
    private final SpringDataPrivacyRequestJpaRepository spring; private final PrivacyRequestPersistenceMapper mapper;
    public JpaPrivacyRequestRepository(SpringDataPrivacyRequestJpaRepository spring, PrivacyRequestPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public PrivacyRequest save(PrivacyRequest r){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public Optional<PrivacyRequest> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<PrivacyRequest> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
