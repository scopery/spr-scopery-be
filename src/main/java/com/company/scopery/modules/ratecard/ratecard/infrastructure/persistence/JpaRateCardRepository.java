package com.company.scopery.modules.ratecard.ratecard.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCard;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecard.infrastructure.mapper.RateCardPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRateCardRepository implements RateCardRepository {
    private final SpringDataRateCardJpaRepository springDataRepository;
    private final RateCardPersistenceMapper mapper;

    public JpaRateCardRepository(SpringDataRateCardJpaRepository springDataRepository,
                                 RateCardPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override public RateCard save(RateCard card) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(card)));
    }
    @Override public Optional<RateCard> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
    @Override public boolean existsByScopeAndCode(RateCardScope scope, UUID organizationId, UUID workspaceId,
                                                 UUID clientId, UUID projectId, String code) {
        return springDataRepository.existsByScopeAndCode(scope.name(), organizationId, workspaceId, clientId, projectId, code);
    }
    @Override public List<RateCard> findActiveDefaultsByWorkspaceId(UUID workspaceId) {
        return springDataRepository.findAllByWorkspaceIdAndIsDefaultTrueAndStatus(workspaceId, RateCardStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }
    @Override public List<RateCard> findActiveByScope(RateCardScope scope, UUID organizationId, UUID workspaceId) {
        List<RateCardJpaEntity> entities = switch (scope) {
            case SYSTEM -> springDataRepository.findAllByScopeAndStatus(scope.name(), RateCardStatus.ACTIVE.name());
            case ORGANIZATION -> springDataRepository.findAllByScopeAndOrganizationIdAndStatus(
                    scope.name(), organizationId, RateCardStatus.ACTIVE.name());
            case WORKSPACE -> springDataRepository.findAllByScopeAndWorkspaceIdAndStatus(
                    scope.name(), workspaceId, RateCardStatus.ACTIVE.name());
            case CLIENT, PROJECT -> springDataRepository.findAllByScopeAndStatus(scope.name(), RateCardStatus.ACTIVE.name());
        };
        return entities.stream().map(mapper::toDomain).toList();
    }
    @Override public PageResult<RateCard> search(RateCardScope scope, UUID organizationId, UUID workspaceId,
                                                  RateCardStatus status, String currency, String code, PageQuery pageQuery) {
        Specification<RateCardJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (scope != null) predicates.add(cb.equal(root.get("scope"), scope.name()));
            if (organizationId != null) predicates.add(cb.equal(root.get("organizationId"), organizationId));
            if (workspaceId != null) predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status.name()));
            if (currency != null && !currency.isBlank()) predicates.add(cb.equal(root.get("defaultCurrencyCode"), currency));
            if (code != null && !code.isBlank()) predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        var pageable = PageRequest.of(pageQuery.page(), pageQuery.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResult.fromSpringPage(springDataRepository.findAll(spec, pageable).map(mapper::toDomain));
    }
}
