package com.company.scopery.modules.aiaction.application.worker;

import com.company.scopery.modules.aiaction.application.orchestrator.AiActionExecutionOrchestrator;
import com.company.scopery.modules.aiaction.application.port.AiActionWorkerLeasePort;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AiActionExecutionWorker {

    private static final Logger log = LoggerFactory.getLogger(AiActionExecutionWorker.class);

    private final String workerId = "worker-" + UUID.randomUUID();

    private final AiActionWorkerLeasePort leasePort;
    private final AiActionExecutionOrchestrator executionOrchestrator;

    @Value("${ai-action.worker.concurrency:4}")
    private int concurrency;

    @Value("${ai-action.worker.lease-seconds:60}")
    private int leaseSeconds;

    public AiActionExecutionWorker(AiActionWorkerLeasePort leasePort,
                                   AiActionExecutionOrchestrator executionOrchestrator) {
        this.leasePort = leasePort;
        this.executionOrchestrator = executionOrchestrator;
    }

    @Scheduled(fixedDelayString = "${ai-action.worker.poll-interval-ms:1000}")
    public void poll() {
        try {
            List<AiActionExecution> claimed = leasePort.claimClaimable(workerId, leaseSeconds, concurrency);
            for (AiActionExecution execution : claimed) {
                try {
                    executionOrchestrator.run(execution.id());
                } catch (Exception e) {
                    log.error("Execution {} failed in worker {}: {}", execution.id(), workerId, e.getMessage(), e);
                } finally {
                    leasePort.releaseLease(execution.id(), workerId);
                }
            }
        } catch (Exception e) {
            log.error("AiActionExecutionWorker poll error: {}", e.getMessage(), e);
        }
    }
}
