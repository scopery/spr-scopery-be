package com.company.scopery.modules.aiassistant.application.jobs;

import com.company.scopery.modules.aiassistant.infrastructure.sse.AiAssistantSseStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AiAssistantHeartbeatJob {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantHeartbeatJob.class);

    private final AiAssistantSseStreamService sseStreamService;

    public AiAssistantHeartbeatJob(AiAssistantSseStreamService sseStreamService) {
        this.sseStreamService = sseStreamService;
    }

    @Scheduled(fixedDelayString = "${scopery.ai-assistant.jobs.heartbeat-fixed-delay-ms:15000}")
    public void sendHeartbeat() {
        try {
            sseStreamService.sendHeartbeat();
        } catch (Exception e) {
            log.warn("[AiAssistantHeartbeatJob] Heartbeat error: {}", e.getMessage());
        }
    }
}
