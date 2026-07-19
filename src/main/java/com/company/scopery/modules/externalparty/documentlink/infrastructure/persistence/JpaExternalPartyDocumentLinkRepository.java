package com.company.scopery.modules.externalparty.documentlink.infrastructure.persistence;

import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLink;
import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLinkRepository;
import com.company.scopery.modules.externalparty.documentlink.infrastructure.mapper.ExternalPartyDocumentLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaExternalPartyDocumentLinkRepository implements ExternalPartyDocumentLinkRepository {
    private final SpringDataExternalPartyDocumentLinkJpaRepository spring;
    private final ExternalPartyDocumentLinkPersistenceMapper mapper;

    public JpaExternalPartyDocumentLinkRepository(SpringDataExternalPartyDocumentLinkJpaRepository spring, ExternalPartyDocumentLinkPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }

    @Override public ExternalPartyDocumentLink save(ExternalPartyDocumentLink l) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(l)));
    }
    @Override public Optional<ExternalPartyDocumentLink> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExternalPartyDocumentLink> findByOrganizationId(UUID workspaceId, UUID orgId) {
        return spring.findByWorkspaceIdAndExternalOrganizationId(workspaceId, orgId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<ExternalPartyDocumentLink> findByContactId(UUID workspaceId, UUID contactId) {
        return spring.findByWorkspaceIdAndExternalContactId(workspaceId, contactId).stream().map(mapper::toDomain).toList();
    }
}
