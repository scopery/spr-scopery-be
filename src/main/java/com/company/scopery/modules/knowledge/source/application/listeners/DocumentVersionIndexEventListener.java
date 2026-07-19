package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.DocumentVersionKnowledgeSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DocumentVersionIndexEventListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentVersionIndexEventListener.class);

    private final DocumentVersionKnowledgeSourceAdapter adapter;
    private final KnowledgeSourceIndexingService indexingService;

    public DocumentVersionIndexEventListener(DocumentVersionKnowledgeSourceAdapter adapter,
                                              KnowledgeSourceIndexingService indexingService) {
        this.adapter = adapter;
        this.indexingService = indexingService;
    }

    @EventListener(condition = "#event['eventCode'] == 'DOCUMENT_VERSION_CREATED' || #event['eventCode'] == 'DOCUMENT_VERSION_APPROVED' || #event['eventCode'] == 'DOCUMENT_VERSION_MARKED_CURRENT'")
    @Async
    public void onVersionAvailable(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID versionId = extractUuid(event, "entityId");
        byte[] bytes = (byte[]) event.get("contentBytes");
        if (projectId == null || versionId == null || bytes == null) return;
        try {
            adapter.buildSnapshotWithBytes(projectId, versionId, bytes).ifPresent(snapshot -> {
                indexingService.upsertSource(snapshot);
                log.debug("Indexed document version source {}", versionId);
            });
        } catch (Exception e) {
            log.warn("Document version index failed for {}: {}", versionId, e.getMessage());
        }
    }

    @EventListener(condition = "#event['eventCode'] == 'DOCUMENT_VERSION_ARCHIVED' || #event['eventCode'] == 'DOCUMENT_ARCHIVED' || #event['eventCode'] == 'DOCUMENT_DELETED_SOFT'")
    @Async
    public void onVersionInvalidated(Map<String, Object> event) {
        UUID workspaceId = extractUuid(event, "workspaceId");
        UUID versionId = extractUuid(event, "entityId");
        if (workspaceId == null || versionId == null) return;
        try {
            indexingService.invalidateSource(workspaceId, versionId);
            log.debug("Invalidated document version source {}", versionId);
        } catch (Exception e) {
            log.warn("Document version invalidation failed for {}: {}", versionId, e.getMessage());
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
