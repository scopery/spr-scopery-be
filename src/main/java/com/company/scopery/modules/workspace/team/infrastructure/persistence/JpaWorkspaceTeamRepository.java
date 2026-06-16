package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.modules.workspace.team.domain.TeamCode;
import com.company.scopery.modules.workspace.team.domain.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.infrastructure.mapper.WorkspaceTeamPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<WorkspaceTeam> findAll(UUID workspaceId, TeamStatus status, Pageable pageable) {
        Specification<WorkspaceTeamJpaEntity> spec = buildSearchSpec(workspaceId, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
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
