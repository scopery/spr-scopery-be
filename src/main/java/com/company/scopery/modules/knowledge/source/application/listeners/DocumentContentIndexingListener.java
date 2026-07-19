package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.NativeDocumentContentSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DocumentContentIndexingListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentContentIndexingListener.class);

    private final NativeDocumentContentSourceAdapter adapter;
    private final KnowledgeSourceIndexingService indexingService;

    public DocumentContentIndexingListener(NativeDocumentContentSourceAdapter adapter,
                                            KnowledgeSourceIndexingService indexingService) {
        this.adapter = adapter;
        this.indexingService = indexingService;
    }

    @EventListener(condition = "#event['eventCode'] == 'DOCUMENT_CONTENT_SAVED'")
    @Async
    public void onContentSaved(Map<String, Object> event) {
        UUID projectId = extractUuid(event, "projectId");
        UUID documentId = extractUuid(event, "documentId");
        if (projectId == null || documentId == null) return;
        try {
            adapter.buildSnapshot(projectId, documentId).ifPresent(snapshot -> {
                indexingService.upsertSource(snapshot);
                log.debug("Indexed native document content for document {}", documentId);
            });
        } catch (Exception e) {
            log.warn("Native document content indexing failed for document {}: {}", documentId, e.getMessage());
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
