package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionWorkerLeasePort;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class StubAiActionWorkerLeasePort implements AiActionWorkerLeasePort {

    private static final Logger log = LoggerFactory.getLogger(StubAiActionWorkerLeasePort.class);

    private final AiActionExecutionRepository executionRepository;

    public StubAiActionWorkerLeasePort(AiActionExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    @Override
    @Transactional
    public List<AiActionExecution> claimClaimable(String workerId, int leaseSeconds, int limit) {
        List<AiActionExecution> candidates = executionRepository.findQueuedOrExpiredLeaseForClaim(limit);
        List<AiActionExecution> claimed = new ArrayList<>();
        Instant leaseExpiry = Instant.now().plusSeconds(leaseSeconds);

        for (AiActionExecution candidate : candidates) {
            try {
                AiActionExecution locked = executionRepository.findAndLockById(candidate.id()).orElse(null);
                if (locked == null) continue;

                boolean claimable = locked.status() == AiActionExecutionStatus.QUEUED
                        || (locked.status() == AiActionExecutionStatus.RUNNING
                                && locked.leaseExpiresAt() != null
                                && locked.leaseExpiresAt().isBefore(Instant.now()));

                if (!claimable) continue;

                locked.claimLease(workerId, leaseExpiry);
                AiActionExecution saved = executionRepository.save(locked);
                claimed.add(saved);

                if (claimed.size() >= limit) break;
            } catch (Exception e) {
                log.warn("[WorkerLease] Failed to claim execution {}: {}", candidate.id(), e.getMessage());
            }
        }
        return claimed;
    }

    @Override
    @Transactional
    public boolean claimById(UUID executionId, String workerId, int leaseSeconds) {
        AiActionExecution exec = executionRepository.findAndLockById(executionId).orElse(null);
        if (exec == null || exec.status() != AiActionExecutionStatus.QUEUED) return false;
        exec.claimLease(workerId, Instant.now().plusSeconds(leaseSeconds));
        executionRepository.save(exec);
        return true;
    }

    @Override
    @Transactional
    public boolean renewLease(UUID executionId, String workerId, int leaseSeconds) {
        AiActionExecution exec = executionRepository.findAndLockById(executionId).orElse(null);
        if (exec == null || !workerId.equals(exec.workerInstanceId())) return false;
        exec.renewLease(Instant.now().plusSeconds(leaseSeconds));
        executionRepository.save(exec);
        return true;
    }

    @Override
    public void releaseLease(UUID executionId, String workerId) {
        // Execution is in terminal state after orchestrator completes — no explicit release needed.
    }
}
