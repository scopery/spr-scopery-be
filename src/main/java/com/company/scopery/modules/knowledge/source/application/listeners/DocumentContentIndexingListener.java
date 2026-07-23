package com.company.scopery.modules.knowledge.source.application.listeners;

import com.company.scopery.common.outbox.PlatformOutboxPublishedEvent;
import com.company.scopery.modules.knowledge.indexing.application.service.KnowledgeSourceIndexingService;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.NativeDocumentContentSourceAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.UUID;

@Component
public class DocumentContentIndexingListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentContentIndexingListener.class);
    private static final String DOCUMENT_CONTENT_SAVED = "DOCUMENT_CONTENT_SAVED";

    private final NativeDocumentContentSourceAdapter adapter;
    private final KnowledgeSourceIndexingService indexingService;
    private final ObjectMapper objectMapper;

    public DocumentContentIndexingListener(NativeDocumentContentSourceAdapter adapter,
                                            KnowledgeSourceIndexingService indexingService,
                                            ObjectMapper objectMapper) {
        this.adapter = adapter;
        this.indexingService = indexingService;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOutboxPublished(PlatformOutboxPublishedEvent event) {
        if (!DOCUMENT_CONTENT_SAVED.equals(event.eventType())) return;

        UUID documentId = event.aggregateId();
        UUID projectId = extractProjectIdFromPayload(event.payloadJson());
        if (documentId == null || projectId == null) {
            log.warn("DocumentContentIndexingListener: missing documentId or projectId in outbox event {}", event.outboxId());
            return;
        }
        try {
            adapter.buildSnapshot(projectId, documentId).ifPresent(snapshot -> {
                indexingService.upsertSource(snapshot);
                log.debug("Indexed native document content for document {}", documentId);
            });
        } catch (Exception e) {
            log.warn("Native document content indexing failed for document {}: {}", documentId, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private UUID extractProjectIdFromPayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) return null;
        try {
            Map<String, Object> envelope = objectMapper.readValue(payloadJson, new TypeReference<>() {});
            Object data = envelope.get("data");
            if (data instanceof Map<?, ?> dataMap) {
                Object projectIdVal = dataMap.get("projectId");
                if (projectIdVal == null) return null;
                return projectIdVal instanceof UUID u ? u : UUID.fromString(projectIdVal.toString());
            }
            return null;
        } catch (Exception e) {
            log.warn("DocumentContentIndexingListener: failed to parse projectId from payload: {}", e.getMessage());
            return null;
        }
    }
}
