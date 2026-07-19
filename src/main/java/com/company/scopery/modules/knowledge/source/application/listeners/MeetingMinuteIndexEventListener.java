package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.MeetingMinuteKnowledgeSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class MeetingMinuteIndexEventListener {

    private static final Logger log = LoggerFactory.getLogger(MeetingMinuteIndexEventListener.class);

    private final MeetingMinuteKnowledgeSourceAdapter adapter;
    private final KnowledgeSourceIndexingService indexingService;

    public MeetingMinuteIndexEventListener(MeetingMinuteKnowledgeSourceAdapter adapter,
                                            KnowledgeSourceIndexingService indexingService) {
        this.adapter = adapter;
        this.indexingService = indexingService;
    }

    @EventListener(condition = "#event['eventCode'] == 'MEETING_MINUTES_SUBMITTED' || #event['eventCode'] == 'MEETING_MINUTES_APPROVED' || #event['eventCode'] == 'MEETING_MINUTES_UPDATED'")
    @Async
    public void onMinutesUpsert(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID minutesId = extractUuid(event, "entityId");
        if (projectId == null || minutesId == null) return;
        try {
            adapter.buildSnapshot(projectId, minutesId).ifPresent(snapshot -> {
                indexingService.upsertSource(snapshot);
                log.debug("Indexed meeting minutes source {}", minutesId);
            });
        } catch (Exception e) {
            log.warn("Meeting minutes index failed for {}: {}", minutesId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'MEETING_MINUTES_ARCHIVED' || #event['eventCode'] == 'MEETING_ARCHIVED'")
    @Async
    public void onMinutesInvalidated(Map<String, Object> event) {
        UUID workspaceId = extractUuid(event, "workspaceId");
        UUID minutesId = extractUuid(event, "entityId");
        if (workspaceId == null || minutesId == null) return;
        try {
            indexingService.invalidateSource(workspaceId, minutesId);
            log.debug("Invalidated meeting minutes source {}", minutesId);
        } catch (Exception e) {
            log.warn("Meeting minutes invalidation failed for {}: {}", minutesId, e.getMessage());
        }
    }

    private UUID extractUuid(Map<String, Object> event, String key) {
        Object val = event.get(key);
        if (val == null) return null;
        try {
            return val instanceof UUID u ? u : UUID.fromString(val.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
