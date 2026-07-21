package com.company.scopery.modules.aiaction.infrastructure.redis;

import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AiActionEventReplayJob {

    private static final Logger log = LoggerFactory.getLogger(AiActionEventReplayJob.class);

    private final AiActionExecutionEventRepository eventRepository;

    public AiActionEventReplayJob(AiActionExecutionEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedDelayString = "${ai-action.redis.replay-delay-ms:30000}")
    public void replayUnpublished() {
        Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
        List<AiActionExecutionEvent> unpublished = eventRepository.findUnpublishedSince(since, 100);
        if (unpublished.isEmpty()) {
            return;
        }
        log.info("AiActionEventReplayJob: {} unpublished events to replay", unpublished.size());
        for (AiActionExecutionEvent event : unpublished) {
            try {
                // TODO: publish to Redis channel
                // For now just mark as published (stub)
                event.markRedisPublished();
                eventRepository.save(event);
            } catch (Exception e) {
                log.warn("Failed to replay event {}: {}", event.id(), e.getMessage());
            }
        }
    }
}
