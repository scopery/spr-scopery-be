package com.company.scopery.modules.externalparty.address.infrastructure.persistence;

import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddress;
import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddressRepository;
import com.company.scopery.modules.externalparty.address.infrastructure.mapper.ExternalPartyAddressPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaExternalPartyAddressRepository implements ExternalPartyAddressRepository {
    private final SpringDataExternalPartyAddressJpaRepository spring;
    private final ExternalPartyAddressPersistenceMapper mapper;
    public JpaExternalPartyAddressRepository(SpringDataExternalPartyAddressJpaRepository spring, ExternalPartyAddressPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ExternalPartyAddress save(ExternalPartyAddress a) {
        var e = mapper.toJpaEntity(a);
        return mapper.toDomain(spring.saveAndFlush(e));
    }
    @Override public Optional<ExternalPartyAddress> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExternalPartyAddress> findByOrganizationId(UUID workspaceId, UUID orgId) { return spring.findByWorkspaceIdAndExternalOrganizationId(workspaceId, orgId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ExternalPartyAddress> findByContactId(UUID workspaceId, UUID contactId) { return spring.findByWorkspaceIdAndExternalContactId(workspaceId, contactId).stream().map(mapper::toDomain).toList(); }
}
