package com.company.scopery.modules.aiagent.execution.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ExecutionLogRepository {

    ExecutionLog save(ExecutionLog executionLog);

    Optional<ExecutionLog> findById(UUID id);

    boolean existsByRequestId(ExecutionRequestId requestId);

    PageResult<ExecutionLog> findAll(String requestId, UUID eventConfigId, UUID eventDefinitionId,
                                     UUID agentId, UUID promptVersionId, UUID modelDeploymentId,
                                     ExecutionTriggerSource triggerSource, ExecutionStatus status,
                                     Instant createdFrom, Instant createdTo, PageQuery pageQuery);

    UsageAggregate aggregateGlobal(Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByEventConfig(UUID eventConfigId, Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByAgent(UUID agentId, Instant windowStart, Instant windowEnd);

    UsageAggregate aggregateByModelDeployment(UUID modelDeploymentId, Instant windowStart, Instant windowEnd);
}
