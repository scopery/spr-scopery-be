package com.company.scopery.modules.ratecard.inflationpolicy.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicy;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
import com.company.scopery.modules.ratecard.inflationpolicy.infrastructure.mapper.InflationPolicyPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaInflationPolicyRepository implements InflationPolicyRepository {
    private final SpringDataInflationPolicyJpaRepository springDataRepository;
    private final InflationPolicyPersistenceMapper mapper;

    public JpaInflationPolicyRepository(SpringDataInflationPolicyJpaRepository springDataRepository,
                                        InflationPolicyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository; this.mapper = mapper;
    }

    @Override public InflationPolicy save(InflationPolicy policy) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(policy)));
    }
    @Override public Optional<InflationPolicy> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
    @Override public boolean existsByScopeAndCode(InflationPolicyScope scope, UUID organizationId, UUID workspaceId, String code) {
        return springDataRepository.existsByScopeAndCode(scope.name(), organizationId, workspaceId, code);
    }
    @Override public List<InflationPolicy> findActiveCovering(InflationPolicyScope scope, UUID organizationId, UUID workspaceId, LocalDate date) {
        return springDataRepository.findActiveCovering(scope.name(), organizationId, workspaceId, date)
                .stream().map(mapper::toDomain).toList();
    }
    @Override public PageResult<InflationPolicy> search(InflationPolicyScope scope, UUID organizationId, UUID workspaceId,
                                                         InflationPolicyStatus status, String code, PageQuery pageQuery) {
        Specification<InflationPolicyJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (scope != null) predicates.add(cb.equal(root.get("scope"), scope.name()));
            if (organizationId != null) predicates.add(cb.equal(root.get("organizationId"), organizationId));
            if (workspaceId != null) predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status.name()));
            if (code != null && !code.isBlank()) predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return PageResult.fromSpringPage(springDataRepository.findAll(spec,
                PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(mapper::toDomain));
    }
}
