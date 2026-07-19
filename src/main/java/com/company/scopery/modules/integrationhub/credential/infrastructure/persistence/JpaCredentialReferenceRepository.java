package com.company.scopery.modules.integrationhub.credential.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.credential.domain.model.*;
import com.company.scopery.modules.integrationhub.credential.infrastructure.mapper.CredentialReferencePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaCredentialReferenceRepository implements CredentialReferenceRepository {
    private final SpringDataCredentialReferenceJpaRepository spring; private final CredentialReferencePersistenceMapper mapper;
    public JpaCredentialReferenceRepository(SpringDataCredentialReferenceJpaRepository spring, CredentialReferencePersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public CredentialReference save(CredentialReference c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(c))); }
    @Override public Optional<CredentialReference> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<CredentialReference> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
