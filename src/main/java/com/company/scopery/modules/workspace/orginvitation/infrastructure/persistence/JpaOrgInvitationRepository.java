package com.company.scopery.modules.workspace.orginvitation.infrastructure.persistence;

import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.orginvitation.infrastructure.mapper.OrgInvitationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrgInvitationRepository implements OrgInvitationRepository {

    private final SpringDataOrgInvitationJpaRepository springDataRepository;
    private final OrgInvitationPersistenceMapper mapper;

    public JpaOrgInvitationRepository(SpringDataOrgInvitationJpaRepository springDataRepository,
                                       OrgInvitationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public OrgInvitation save(OrgInvitation invitation) {
        OrgInvitationJpaEntity entity = mapper.toJpaEntity(invitation);
        OrgInvitationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<OrgInvitation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<OrgInvitation> findByToken(String token) {
        return springDataRepository.findByToken(token).map(mapper::toDomain);
    }
}
