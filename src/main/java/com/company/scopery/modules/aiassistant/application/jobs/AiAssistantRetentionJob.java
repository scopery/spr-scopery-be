package com.company.scopery.modules.aiassistant.application.jobs;

import com.company.scopery.modules.aiassistant.application.port.AiAssistantRetentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AiAssistantRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantRetentionJob.class);

    private final AiAssistantRetentionService retentionService;

    public AiAssistantRetentionJob(AiAssistantRetentionService retentionService) {
        this.retentionService = retentionService;
    }

    @Scheduled(cron = "${scopery.ai-assistant.jobs.retention-cron:0 0 3 * * *}")
    public void runRetention() {
        int softDeleted = retentionService.softDeleteExpiredConversations();
        int purged = retentionService.purgeDeletedConversations();
        log.info("[AiAssistantRetentionJob] Soft-deleted {} conversations, purged {} deleted conversations",
                softDeleted, purged);
    }
}
