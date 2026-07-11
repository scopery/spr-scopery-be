package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.team.domain.valueobject.TeamCode;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.infrastructure.mapper.WorkspaceTeamPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkspaceTeamRepository implements TeamRepository {

    private final SpringDataWorkspaceTeamJpaRepository springDataRepository;
    private final WorkspaceTeamPersistenceMapper mapper;

    public JpaWorkspaceTeamRepository(SpringDataWorkspaceTeamJpaRepository springDataRepository,
                                       WorkspaceTeamPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceTeam save(WorkspaceTeam team) {
        WorkspaceTeamJpaEntity entity = mapper.toJpaEntity(team);
        WorkspaceTeamJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<WorkspaceTeam> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkspaceIdAndCode(UUID workspaceId, TeamCode code) {
        return springDataRepository.existsByWorkspaceIdAndCode(workspaceId, code.value());
    }

    @Override
    public PageResult<WorkspaceTeam> findAll(UUID workspaceId, TeamStatus status, PageQuery pageQuery) {
        Specification<WorkspaceTeamJpaEntity> spec = buildSearchSpec(workspaceId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<WorkspaceTeam> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<WorkspaceTeamJpaEntity> buildSearchSpec(UUID workspaceId, TeamStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
