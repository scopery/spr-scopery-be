package com.company.scopery.modules.aiassistant.application.jobs;

import com.company.scopery.modules.aiassistant.domain.enums.MessageStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AiAssistantStaleMessageRecoveryJob {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantStaleMessageRecoveryJob.class);
    private static final int STALE_THRESHOLD_MINUTES = 10;

    private final AiMessageRepository messageRepository;

    public AiAssistantStaleMessageRecoveryJob(AiMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Scheduled(fixedDelayString = "${scopery.ai-assistant.jobs.stale-recovery-fixed-delay-ms:300000}")
    @Transactional
    public void recoverStaleMessages() {
        Instant threshold = Instant.now().minus(STALE_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        List<AiMessage> stale = messageRepository.findByStatusInAndCreatedAtBefore(
                List.of(MessageStatus.QUEUED, MessageStatus.CONTEXTUALIZING), threshold);
        for (AiMessage msg : stale) {
            msg.markFailed("STALE_RECOVERY", "Message stuck in non-terminal state for >" + STALE_THRESHOLD_MINUTES + " min");
            messageRepository.save(msg);
        }
        if (!stale.isEmpty()) {
            log.warn("[AiAssistantStaleMessageRecoveryJob] Recovered {} stale messages", stale.size());
        }
    }
}
