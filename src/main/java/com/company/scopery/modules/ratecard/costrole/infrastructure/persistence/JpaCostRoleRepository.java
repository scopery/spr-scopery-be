package com.company.scopery.modules.ratecard.costrole.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.costrole.infrastructure.mapper.CostRolePersistenceMapper;
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
public class JpaCostRoleRepository implements CostRoleRepository {

    private final SpringDataCostRoleJpaRepository springDataRepository;
    private final CostRolePersistenceMapper mapper;

    public JpaCostRoleRepository(SpringDataCostRoleJpaRepository springDataRepository,
                                 CostRolePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public CostRole save(CostRole role) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(role)));
    }

    @Override
    public Optional<CostRole> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CostRole> findByCode(String code) {
        return springDataRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsByScopeAndCode(CostRoleScope scope, UUID organizationId, UUID workspaceId, String code) {
        return springDataRepository.existsByScopeAndCode(scope.name(), organizationId, workspaceId, code);
    }

    @Override
    public boolean isReferencedByRateLines(UUID costRoleId) {
        return springDataRepository.existsLineByCostRoleId(costRoleId);
    }

    @Override
    public PageResult<CostRole> search(CostRoleScope scope, UUID organizationId, UUID workspaceId,
                                       CostRoleStatus status, String category, String code, PageQuery pageQuery) {
        Specification<CostRoleJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (scope != null) {
                predicates.add(cb.equal(root.get("scope"), scope.name()));
            }
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId"), organizationId));
            }
            if (workspaceId != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("workspaceId"), workspaceId),
                        cb.equal(root.get("scope"), CostRoleScope.SYSTEM.name()),
                        cb.and(
                                cb.equal(root.get("scope"), CostRoleScope.ORGANIZATION.name()),
                                cb.equal(root.get("organizationId"), organizationId)
                        )
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (code != null && !code.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
        Page<CostRole> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }
}
