package com.company.scopery.modules.aiagent.execution.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.model.UsageAggregate;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;
import com.company.scopery.modules.aiagent.execution.infrastructure.mapper.ExecutionLogPersistenceMapper;
import com.company.scopery.modules.aiagent.execution.infrastructure.persistence.entity.ExecutionLogJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaExecutionLogRepository implements ExecutionLogRepository {

    private final SpringDataExecutionLogJpaRepository springDataRepository;
    private final ExecutionLogPersistenceMapper mapper;

    public JpaExecutionLogRepository(SpringDataExecutionLogJpaRepository springDataRepository,
                                      ExecutionLogPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ExecutionLog save(ExecutionLog executionLog) {
        ExecutionLogJpaEntity entity = mapper.toJpaEntity(executionLog);
        ExecutionLogJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ExecutionLog> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByRequestId(ExecutionRequestId requestId) {
        return springDataRepository.existsByRequestId(requestId.value());
    }

    @Override
    public PageResult<ExecutionLog> findAll(String requestId, UUID eventConfigId, UUID eventDefinitionId,
                                            UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                                            ExecutionTriggerSource triggerSource, ExecutionStatus status,
                                            Instant createdFrom, Instant createdTo, PageQuery pageQuery) {
        Specification<ExecutionLogJpaEntity> spec = buildSearchSpec(requestId, eventConfigId,
                eventDefinitionId, agentId, promptVersionId, modelDeploymentId,
                triggerSource, status, createdFrom, createdTo);
        Pageable pageable = toPageable(pageQuery);
        Page<ExecutionLog> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    @Override
    public UsageAggregate aggregateGlobal(Instant windowStart, Instant windowEnd) {
        long count = springDataRepository.countRequestsGlobal(windowStart, windowEnd);
        Long tokens = springDataRepository.sumTotalTokensGlobal(windowStart, windowEnd);
        BigDecimal cost = springDataRepository.sumEstimatedCostGlobal(windowStart, windowEnd);
        return new UsageAggregate(count,
                tokens != null ? tokens : 0L,
                cost != null ? cost : BigDecimal.ZERO);
    }

    @Override
    public UsageAggregate aggregateByEventConfig(UUID eventConfigId, Instant windowStart, Instant windowEnd) {
        long count = springDataRepository.countRequestsByEventConfig(eventConfigId, windowStart, windowEnd);
        Long tokens = springDataRepository.sumTotalTokensByEventConfig(eventConfigId, windowStart, windowEnd);
        BigDecimal cost = springDataRepository.sumEstimatedCostByEventConfig(eventConfigId, windowStart, windowEnd);
        return new UsageAggregate(count,
                tokens != null ? tokens : 0L,
                cost != null ? cost : BigDecimal.ZERO);
    }

    @Override
    public UsageAggregate aggregateByAgent(UUID agentId, Instant windowStart, Instant windowEnd) {
        long count = springDataRepository.countRequestsByAgent(agentId, windowStart, windowEnd);
        Long tokens = springDataRepository.sumTotalTokensByAgent(agentId, windowStart, windowEnd);
        BigDecimal cost = springDataRepository.sumEstimatedCostByAgent(agentId, windowStart, windowEnd);
        return new UsageAggregate(count,
                tokens != null ? tokens : 0L,
                cost != null ? cost : BigDecimal.ZERO);
    }

    @Override
    public UsageAggregate aggregateByModelDeployment(UUID modelDeploymentId, Instant windowStart, Instant windowEnd) {
        long count = springDataRepository.countRequestsByModelDeployment(modelDeploymentId, windowStart, windowEnd);
        Long tokens = springDataRepository.sumTotalTokensByModelDeployment(modelDeploymentId, windowStart, windowEnd);
        BigDecimal cost = springDataRepository.sumEstimatedCostByModelDeployment(modelDeploymentId, windowStart, windowEnd);
        return new UsageAggregate(count,
                tokens != null ? tokens : 0L,
                cost != null ? cost : BigDecimal.ZERO);
    }

    private Specification<ExecutionLogJpaEntity> buildSearchSpec(String requestId,
                                                                   UUID eventConfigId,
                                                                   UUID eventDefinitionId,
                                                                   UUID agentId,
                                                                   UUID promptVersionId,
                                                                   UUID modelDeploymentId,
                                                                   ExecutionTriggerSource triggerSource,
                                                                   ExecutionStatus status,
                                                                   Instant createdFrom,
                                                                   Instant createdTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestId != null && !requestId.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("requestId")),
                        "%" + requestId.trim().toLowerCase() + "%"));
            }
            if (eventConfigId != null) {
                predicates.add(cb.equal(root.get("eventConfigId"), eventConfigId));
            }
            if (eventDefinitionId != null) {
                predicates.add(cb.equal(root.get("eventDefinitionId"), eventDefinitionId));
            }
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }
            if (promptVersionId != null) {
                predicates.add(cb.equal(root.get("promptVersionId"), promptVersionId));
            }
            if (modelDeploymentId != null) {
                predicates.add(cb.equal(root.get("modelDeploymentId"), modelDeploymentId));
            }
            if (triggerSource != null) {
                predicates.add(cb.equal(root.get("triggerSource"), triggerSource.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (createdFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}