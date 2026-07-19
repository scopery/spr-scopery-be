package com.company.scopery.modules.clientportal.account.infrastructure.persistence;
import com.company.scopery.modules.clientportal.account.domain.model.*;
import com.company.scopery.modules.clientportal.account.infrastructure.mapper.ExternalPortalAccountPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaExternalPortalAccountRepository implements ExternalPortalAccountRepository {
    private final SpringDataExternalPortalAccountJpaRepository springData;
    private final ExternalPortalAccountPersistenceMapper mapper;
    public JpaExternalPortalAccountRepository(SpringDataExternalPortalAccountJpaRepository springData, ExternalPortalAccountPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ExternalPortalAccount save(ExternalPortalAccount e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ExternalPortalAccount> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<ExternalPortalAccount> findByWorkspaceIdAndEmail(UUID workspaceId, String email) {
        return springData.findByWorkspaceIdAndEmailIgnoreCase(workspaceId, email).map(mapper::toDomain);
    }
}
