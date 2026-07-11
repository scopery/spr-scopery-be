package com.company.scopery.modules.project.projectphase.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.projectphase.infrastructure.mapper.ProjectPhasePersistenceMapper;
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
public class JpaProjectPhaseRepository implements ProjectPhaseRepository {

    private final SpringDataProjectPhaseJpaRepository springDataRepository;
    private final ProjectPhasePersistenceMapper mapper;

    public JpaProjectPhaseRepository(SpringDataProjectPhaseJpaRepository springDataRepository,
                                     ProjectPhasePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectPhase save(ProjectPhase phase) {
        ProjectPhaseJpaEntity entity = mapper.toJpaEntity(phase);
        ProjectPhaseJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProjectPhase> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springDataRepository.existsByProjectIdAndCode(projectId, code);
    }

    @Override
    public boolean existsByProjectIdAndDisplayOrder(UUID projectId, int displayOrder) {
        return springDataRepository.existsByProjectIdAndDisplayOrder(projectId, displayOrder);
    }

    @Override
    public boolean hasActiveWbsNodesOrTasks(UUID phaseId) {
        return springDataRepository.hasActiveWbsNodes(phaseId)
                || springDataRepository.hasActiveTasks(phaseId);
    }

    @Override
    public PageResult<ProjectPhase> search(UUID projectId, ProjectPhaseStatus status, PageQuery pageQuery) {
        Specification<ProjectPhaseJpaEntity> spec = buildSearchSpec(projectId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<ProjectPhase> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public List<ProjectPhase> findAllByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ProjectPhaseJpaEntity> buildSearchSpec(UUID projectId, ProjectPhaseStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
