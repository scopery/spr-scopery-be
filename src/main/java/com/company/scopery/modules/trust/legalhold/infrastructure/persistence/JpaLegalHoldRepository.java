package com.company.scopery.modules.trust.legalhold.infrastructure.persistence;
import com.company.scopery.modules.trust.legalhold.domain.model.*;
import com.company.scopery.modules.trust.legalhold.infrastructure.mapper.LegalHoldPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaLegalHoldRepository implements LegalHoldRepository {
    private final SpringDataLegalHoldJpaRepository spring; private final LegalHoldPersistenceMapper mapper;
    public JpaLegalHoldRepository(SpringDataLegalHoldJpaRepository spring, LegalHoldPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public LegalHold save(LegalHold h){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(h))); }
    @Override public Optional<LegalHold> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<LegalHold> findByWorkspaceId(UUID workspaceId){
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<LegalHold> findActiveByWorkspaceId(UUID workspaceId){
        return spring.findByWorkspaceIdAndStatus(workspaceId, "ACTIVE").stream().map(mapper::toDomain).toList();
    }
}
