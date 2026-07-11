package com.company.scopery.modules.project.phasedefinition.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionScope;
import com.company.scopery.modules.project.phasedefinition.domain.enums.PhaseDefinitionStatus;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinition;
import com.company.scopery.modules.project.phasedefinition.domain.model.PhaseDefinitionRepository;
import com.company.scopery.modules.project.phasedefinition.infrastructure.mapper.PhaseDefinitionPersistenceMapper;
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
public class JpaPhaseDefinitionRepository implements PhaseDefinitionRepository {

    private final SpringDataPhaseDefinitionJpaRepository springDataRepository;
    private final PhaseDefinitionPersistenceMapper mapper;

    public JpaPhaseDefinitionRepository(SpringDataPhaseDefinitionJpaRepository springDataRepository,
                                         PhaseDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public PhaseDefinition save(PhaseDefinition pd) {
        PhaseDefinitionJpaEntity entity = mapper.toJpaEntity(pd);
        PhaseDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PhaseDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndScope(String code, PhaseDefinitionScope scope) {
        return springDataRepository.existsByCodeAndScope(code, scope.name());
    }

    @Override
    public boolean existsByCodeAndScopeAndWorkspaceId(String code, PhaseDefinitionScope scope, UUID workspaceId) {
        return springDataRepository.existsByCodeAndScopeAndWorkspaceId(code, scope.name(), workspaceId);
    }

    @Override
    public boolean isUsedByAnyProject(UUID phaseDefinitionId) {
        return springDataRepository.isUsedByAnyProject(phaseDefinitionId);
    }

    @Override
    public PageResult<PhaseDefinition> search(
            PhaseDefinitionScope scope,
            UUID workspaceId,
            String keyword,
            PhaseDefinitionStatus status,
            PageQuery pageQuery) {

        Specification<PhaseDefinitionJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (scope != null) {
                predicates.add(cb.equal(root.get("scope"), scope.name()));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = toPageable(pageQuery);
        Page<PhaseDefinition> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }
}
