package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeamMember;
import com.company.scopery.modules.workspace.team.infrastructure.mapper.WorkspaceTeamMemberPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResult<WorkspaceTeamMember> findByTeamIdPageable(UUID teamId, PageQuery pageQuery) {
        Pageable pageable = toPageable(pageQuery);
        Page<WorkspaceTeamMember> page = springDataRepository.findByTeamId(teamId, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }
}
