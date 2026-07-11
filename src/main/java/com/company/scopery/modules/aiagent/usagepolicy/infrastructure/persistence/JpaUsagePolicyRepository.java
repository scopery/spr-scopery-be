package com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;
import com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence.entity.UsagePolicyJpaEntity;
import com.company.scopery.modules.aiagent.usagepolicy.infrastructure.mapper.UsagePolicyPersistenceMapper;
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
public class JpaUsagePolicyRepository implements UsagePolicyRepository {

    private final SpringDataUsagePolicyJpaRepository springDataRepository;
    private final UsagePolicyPersistenceMapper mapper;

    public JpaUsagePolicyRepository(SpringDataUsagePolicyJpaRepository springDataRepository,
                                     UsagePolicyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public UsagePolicy save(UsagePolicy policy) {
        UsagePolicyJpaEntity entity = mapper.toJpaEntity(policy);
        UsagePolicyJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UsagePolicy> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(UsagePolicyCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsActiveByTargetTypeAndTargetId(UsagePolicyTargetType targetType,
                                                        UUID targetId, UUID excludeId) {
        Specification<UsagePolicyJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("targetType"), targetType.name()));
            if (targetId != null) {
                predicates.add(cb.equal(root.get("targetId"), targetId));
            } else {
                predicates.add(cb.isNull(root.get("targetId")));
            }
            predicates.add(cb.equal(root.get("status"), UsagePolicyStatus.ACTIVE.name()));
            if (excludeId != null) {
                predicates.add(cb.notEqual(root.get("id"), excludeId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return springDataRepository.count(spec) > 0;
    }

    @Override
    public PageResult<UsagePolicy> findAll(String keyword, UsagePolicyTargetType targetType,
                                      UsagePolicyStatus status, PageQuery pageQuery) {
        Specification<UsagePolicyJpaEntity> spec = buildSearchSpec(keyword, targetType, status);
        Pageable pageable = toPageable(pageQuery);
        Page<UsagePolicy> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    @Override
    public List<UsagePolicy> findApplicableActivePolicies(UUID eventConfigId, UUID agentId, UUID modelDeploymentId) {
        List<UsagePolicyJpaEntity> entities = new ArrayList<>();
        entities.addAll(springDataRepository.findByStatusAndTargetType("ACTIVE", "GLOBAL"));
        if (eventConfigId != null) {
            entities.addAll(springDataRepository.findByStatusAndTargetTypeAndTargetId(
                    "ACTIVE", "EVENT_CONFIG", eventConfigId));
        }
        if (agentId != null) {
            entities.addAll(springDataRepository.findByStatusAndTargetTypeAndTargetId(
                    "ACTIVE", "AGENT", agentId));
        }
        if (modelDeploymentId != null) {
            entities.addAll(springDataRepository.findByStatusAndTargetTypeAndTargetId(
                    "ACTIVE", "MODEL_DEPLOYMENT", modelDeploymentId));
        }
        return entities.stream().map(mapper::toDomain).toList();
    }

    private Specification<UsagePolicyJpaEntity> buildSearchSpec(String keyword,
                                                                  UsagePolicyTargetType targetType,
                                                                  UsagePolicyStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toUpperCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.upper(root.get("name")), pattern),
                        cb.like(root.get("code"), pattern)
                ));
            }
            if (targetType != null) {
                predicates.add(cb.equal(root.get("targetType"), targetType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
