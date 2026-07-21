package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionWorkerLeasePort;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

// Stub implementation — replaced in Step 14
@Component
public class StubAiActionWorkerLeasePort implements AiActionWorkerLeasePort {

    @Override
    public List<AiActionExecution> claimClaimable(String workerId, int leaseSeconds, int limit) {
        return List.of();
    }

    @Override
    public boolean renewLease(UUID executionId, String workerId, int leaseSeconds) {
        return false;
    }

    @Override
    public void releaseLease(UUID executionId, String workerId) {
        // no-op
    }
}
