package com.company.scopery.modules.aiassistant.application.jobs;

import com.company.scopery.modules.aiassistant.application.port.AiAssistantRetentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AiAssistantStreamEventPurgeJob {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantStreamEventPurgeJob.class);

    private final AiAssistantRetentionService retentionService;

    public AiAssistantStreamEventPurgeJob(AiAssistantRetentionService retentionService) {
        this.retentionService = retentionService;
    }

    @Scheduled(cron = "${scopery.ai-assistant.jobs.stream-event-purge-cron:0 0 * * * *}")
    public void purgeExpiredStreamEvents() {
        int deleted = retentionService.purgeExpiredStreamEvents();
        log.info("[AiAssistantStreamEventPurgeJob] Purged {} expired stream events", deleted);
    }
}
