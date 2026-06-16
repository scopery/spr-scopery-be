package com.company.scopery.modules.aiagent.execution.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ExecutionLogRepository {

    ExecutionLog save(ExecutionLog executionLog);

    Optional<ExecutionLog> findById(UUID id);

    boolean existsByRequestId(ExecutionRequestId requestId);

    Page<ExecutionLog> findAll(String requestId, UUID eventConfigId, UUID eventDefinitionId,
                               UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                               ExecutionTriggerSource triggerSource, ExecutionStatus status,
                               Instant createdFrom, Instant createdTo, Pageable pageable);

    UsageAggregate aggregateGlobal(Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByEventConfig(UUID eventConfigId, Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByAgent(UUID agentId, Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByModelDeployment(UUID modelDeploymentId, Instant windowStart, Instant windowEnd);
}
