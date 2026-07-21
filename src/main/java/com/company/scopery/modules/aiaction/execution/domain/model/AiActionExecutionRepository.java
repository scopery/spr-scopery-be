package com.company.scopery.modules.aiaction.execution.domain.model;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionExecutionRepository {

    AiActionExecution save(AiActionExecution execution);

    Optional<AiActionExecution> findById(UUID id);

    Optional<AiActionExecution> findByPlanId(UUID planId);

    Optional<AiActionExecution> findByExecutionKey(String executionKey);

    int countActiveByUserAndWorkspace(UUID userId, UUID workspaceId, List<AiActionExecutionStatus> activeStatuses);

    List<AiActionExecution> findQueuedOrExpiredLeaseForClaim(int limit);

    Optional<AiActionExecution> findAndLockById(UUID id);
}
