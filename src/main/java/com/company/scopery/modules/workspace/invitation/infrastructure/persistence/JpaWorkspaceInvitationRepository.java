package com.company.scopery.modules.workspace.invitation.infrastructure.persistence;

import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.invitation.infrastructure.mapper.WorkspaceInvitationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkspaceInvitationRepository implements WorkspaceInvitationRepository {

    private final SpringDataWorkspaceInvitationJpaRepository springDataRepository;
    private final WorkspaceInvitationPersistenceMapper mapper;

    public JpaWorkspaceInvitationRepository(SpringDataWorkspaceInvitationJpaRepository springDataRepository,
                                             WorkspaceInvitationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceInvitation save(WorkspaceInvitation invitation) {
        WorkspaceInvitationJpaEntity entity = mapper.toJpaEntity(invitation);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<WorkspaceInvitation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<WorkspaceInvitation> findByCodeHash(String codeHash) {
        return springDataRepository.findByInvitationCodeHash(codeHash).map(mapper::toDomain);
    }

    @Override
    public List<WorkspaceInvitation> findByWorkspaceId(UUID workspaceId) {
        return springDataRepository.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
