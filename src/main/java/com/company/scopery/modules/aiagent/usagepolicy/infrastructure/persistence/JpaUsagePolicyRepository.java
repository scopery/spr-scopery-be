package com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence;

import com.company.scopery.modules.aiagent.usagepolicy.domain.*;
import com.company.scopery.modules.aiagent.usagepolicy.infrastructure.mapper.UsagePolicyPersistenceMapper;
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
    public Page<UsagePolicy> findAll(String keyword, UsagePolicyTargetType targetType,
                                      UsagePolicyStatus status, Pageable pageable) {
        Specification<UsagePolicyJpaEntity> spec = buildSearchSpec(keyword, targetType, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
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
