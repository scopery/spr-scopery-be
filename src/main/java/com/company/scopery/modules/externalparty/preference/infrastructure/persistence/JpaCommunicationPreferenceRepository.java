package com.company.scopery.modules.externalparty.preference.infrastructure.persistence;

import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreference;
import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreferenceRepository;
import com.company.scopery.modules.externalparty.preference.infrastructure.mapper.CommunicationPreferencePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCommunicationPreferenceRepository implements CommunicationPreferenceRepository {
    private final SpringDataCommunicationPreferenceJpaRepository spring;
    private final CommunicationPreferencePersistenceMapper mapper;

    public JpaCommunicationPreferenceRepository(SpringDataCommunicationPreferenceJpaRepository spring, CommunicationPreferencePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }

    @Override public CommunicationPreference save(CommunicationPreference p) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p)));
    }
    @Override public Optional<CommunicationPreference> findByOrganizationId(UUID workspaceId, UUID orgId) {
        return spring.findByWorkspaceIdAndExternalOrganizationId(workspaceId, orgId).map(mapper::toDomain);
    }
    @Override public Optional<CommunicationPreference> findByContactId(UUID workspaceId, UUID contactId) {
        return spring.findByWorkspaceIdAndExternalContactId(workspaceId, contactId).map(mapper::toDomain);
    }
}
