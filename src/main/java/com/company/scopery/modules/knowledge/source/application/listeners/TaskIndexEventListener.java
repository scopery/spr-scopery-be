package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.TaskKnowledgeSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TaskIndexEventListener {

    private static final Logger log = LoggerFactory.getLogger(TaskIndexEventListener.class);

    private final TaskKnowledgeSourceAdapter adapter;
    private final KnowledgeSourceIndexingService indexingService;

    public TaskIndexEventListener(TaskKnowledgeSourceAdapter adapter,
                                   KnowledgeSourceIndexingService indexingService) {
        this.adapter = adapter;
        this.indexingService = indexingService;
    }

    @EventListener(condition = "#event['eventCode'] == 'TASK_CREATED' || #event['eventCode'] == 'TASK_UPDATED' || #event['eventCode'] == 'TASK_ASSIGNED' || #event['eventCode'] == 'TASK_STARTED' || #event['eventCode'] == 'TASK_COMPLETED' || #event['eventCode'] == 'TASK_BLOCKED' || #event['eventCode'] == 'TASK_CANCELLED'")
    @Async
    public void onTaskUpsert(Map<String, Object> event) {
        UUID taskId = extractUuid(event, "entityId");
        if (taskId == null) return;
        try {
            adapter.buildSnapshot(taskId).ifPresent(snapshot -> {
                indexingService.upsertSource(snapshot);
                log.debug("Indexed task source {}", taskId);
            });
        } catch (Exception e) {
            log.warn("Task index failed for {}: {}", taskId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'TASK_ARCHIVED'")
    @Async
    public void onTaskArchived(Map<String, Object> event) {
        UUID taskId = extractUuid(event, "entityId");
        UUID workspaceId = extractUuid(event, "workspaceId");
        if (taskId == null || workspaceId == null) return;
        try {
            indexingService.invalidateSource(workspaceId, taskId);
            log.debug("Invalidated task source {}", taskId);
        } catch (Exception e) {
            log.warn("Task invalidation failed for {}: {}", taskId, e.getMessage());
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
