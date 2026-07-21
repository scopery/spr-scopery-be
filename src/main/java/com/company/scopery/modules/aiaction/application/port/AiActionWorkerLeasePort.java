package com.company.scopery.modules.aiaction.application.port;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;

import java.util.List;
import java.util.UUID;

public interface AiActionWorkerLeasePort {

    List<AiActionExecution> claimClaimable(String workerId, int leaseSeconds, int limit);

    boolean renewLease(UUID executionId, String workerId, int leaseSeconds);

    void releaseLease(UUID executionId, String workerId);
}
