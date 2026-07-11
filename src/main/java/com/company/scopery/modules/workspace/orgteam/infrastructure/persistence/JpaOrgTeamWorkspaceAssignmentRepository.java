package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignmentRepository;
import com.company.scopery.modules.workspace.orgteam.infrastructure.mapper.OrgTeamWorkspaceAssignmentPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrgTeamWorkspaceAssignmentRepository implements OrgTeamWorkspaceAssignmentRepository {

    private final SpringDataOrgTeamWorkspaceAssignmentJpaRepository springDataRepository;
    private final OrgTeamWorkspaceAssignmentPersistenceMapper mapper;

    public JpaOrgTeamWorkspaceAssignmentRepository(
            SpringDataOrgTeamWorkspaceAssignmentJpaRepository springDataRepository,
            OrgTeamWorkspaceAssignmentPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public OrgTeamWorkspaceAssignment save(OrgTeamWorkspaceAssignment assignment) {
        OrgTeamWorkspaceAssignmentJpaEntity entity = mapper.toJpaEntity(assignment);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<OrgTeamWorkspaceAssignment> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByTeamIdAndWorkspaceIdAndStatus(UUID teamId, UUID workspaceId, String status) {
        return springDataRepository.existsByTeamIdAndWorkspaceIdAndStatus(teamId, workspaceId, status);
    }

    @Override
    public Optional<OrgTeamWorkspaceAssignment> findByTeamIdAndWorkspaceId(UUID teamId, UUID workspaceId) {
        return springDataRepository.findByTeamIdAndWorkspaceId(teamId, workspaceId).map(mapper::toDomain);
    }

    @Override
    public PageResult<OrgTeamWorkspaceAssignment> findAllByTeamId(UUID teamId, PageQuery pageQuery) {
        Pageable pageable = toPageable(pageQuery);
        Page<OrgTeamWorkspaceAssignment> page = springDataRepository.findAllByTeamId(teamId, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }
}
