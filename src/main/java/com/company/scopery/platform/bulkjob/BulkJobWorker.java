package com.company.scopery.platform.bulkjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BulkJobWorker {

    private static final Logger log = LoggerFactory.getLogger(BulkJobWorker.class);

    private final String workerId = "bulk-worker-" + UUID.randomUUID().toString().substring(0, 8);

    private final BulkJobService bulkJobService;
    private final Map<String, BulkJobHandler> handlerMap;

    @Value("${scopery.bulk-job.concurrency:3}")
    private int concurrency;

    @Value("${scopery.bulk-job.lease-seconds:300}")
    private int leaseSeconds;

    public BulkJobWorker(BulkJobService bulkJobService, List<BulkJobHandler> handlers) {
        this.bulkJobService = bulkJobService;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(BulkJobHandler::supportsJobType, Function.identity()));
    }

    @Scheduled(fixedDelayString = "${scopery.bulk-job.poll-interval-ms:3000}")
    public void poll() {
        try {
            List<BulkJobRecord> claimed = bulkJobService.claimQueued(workerId, leaseSeconds, concurrency);
            for (BulkJobRecord job : claimed) {
                processJob(job);
            }
        } catch (Exception e) {
            log.error("[BulkJobWorker] Poll error: {}", e.getMessage(), e);
        }
    }

    private void processJob(BulkJobRecord job) {
        BulkJobHandler handler = handlerMap.get(job.jobType());
        if (handler == null) {
            log.error("[BulkJobWorker] No handler for job type '{}', jobId={}", job.jobType(), job.id());
            bulkJobService.markFailed(job.id(), "No handler registered for job type: " + job.jobType());
            return;
        }
        try {
            log.info("[BulkJobWorker] Processing jobId={} type={} items={}", job.id(), job.jobType(), job.totalItems());
            handler.process(job);
            log.info("[BulkJobWorker] Completed jobId={}", job.id());
        } catch (Exception e) {
            log.error("[BulkJobWorker] Job {} failed: {}", job.id(), e.getMessage(), e);
            bulkJobService.markFailed(job.id(), e.getMessage());
        }
    }
}
