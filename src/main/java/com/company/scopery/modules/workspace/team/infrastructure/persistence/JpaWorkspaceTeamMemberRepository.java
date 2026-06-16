package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.modules.workspace.team.domain.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;
import com.company.scopery.modules.workspace.team.infrastructure.mapper.WorkspaceTeamMemberPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaWorkspaceTeamMemberRepository implements TeamMemberRepository {

    private final SpringDataWorkspaceTeamMemberJpaRepository springDataRepository;
    private final WorkspaceTeamMemberPersistenceMapper mapper;

    public JpaWorkspaceTeamMemberRepository(SpringDataWorkspaceTeamMemberJpaRepository springDataRepository,
                                             WorkspaceTeamMemberPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceTeamMember save(WorkspaceTeamMember member) {
        WorkspaceTeamMemberJpaEntity entity = mapper.toJpaEntity(member);
        // Set createdAt from domain to ensure Spring Data uses merge() consistently
        entity.setCreatedAt(member.createdAt());
        WorkspaceTeamMemberJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByTeamIdAndUserId(UUID teamId, UUID userId) {
        return springDataRepository.existsByTeamIdAndUserId(teamId, userId);
    }

    @Override
    @Transactional
    public void delete(UUID teamId, UUID userId) {
        springDataRepository.deleteByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public List<WorkspaceTeamMember> findByTeamId(UUID teamId) {
        return springDataRepository.findByTeamId(teamId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkspaceTeamMember> findByUserId(UUID userId) {
        return springDataRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<WorkspaceTeamMember> findByTeamIdPageable(UUID teamId, Pageable pageable) {
        return springDataRepository.findByTeamId(teamId, pageable).map(mapper::toDomain);
    }
}
