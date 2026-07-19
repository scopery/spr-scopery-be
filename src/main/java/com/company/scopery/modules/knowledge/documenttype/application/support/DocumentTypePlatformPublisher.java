package com.company.scopery.modules.knowledge.documenttype.application.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;
import com.company.scopery.modules.knowledge.shared.listeners.KnowledgeEventDefinitionSeedInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DocumentTypePlatformPublisher {

    public static final String AGGREGATE_TYPE = "DOCUMENT_TYPE";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;
    private final ObjectMapper objectMapper;

    public DocumentTypePlatformPublisher(TransactionalOutboxService outboxService,
                                         ImmutableAuditEventService auditEventService,
                                         ObjectMapper objectMapper) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
        this.objectMapper = objectMapper;
    }

    public void enqueue(DocumentType dt, String eventCode) {
        outboxService.enqueue(AGGREGATE_TYPE, dt.id(), eventCode,
                KnowledgeEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, payload(dt));
    }

    public void auditClassificationChanged(UUID actorId, DocumentType before, DocumentType after) {
        auditEventService.record(
                AuditEventType.DOCUMENT_TYPE_CLASSIFICATION_CHANGED,
                actorId,
                "USER",
                AGGREGATE_TYPE,
                after.id(),
                after.organizationId(),
                after.workspaceId(),
                Map.of("defaultClassification",
                        before.defaultClassification() != null ? before.defaultClassification().name() : null),
                Map.of("defaultClassification",
                        after.defaultClassification() != null ? after.defaultClassification().name() : null),
                "Document type default classification changed");
    }

    public void auditSchemaChanged(UUID actorId, DocumentType before, DocumentType after) {
        auditEventService.record(
                AuditEventType.DOCUMENT_TYPE_SCHEMA_CHANGED,
                actorId,
                "USER",
                AGGREGATE_TYPE,
                after.id(),
                after.organizationId(),
                after.workspaceId(),
                Map.of("metadataSchemaJson", before.metadataSchemaJson()),
                Map.of("metadataSchemaJson", after.metadataSchemaJson()),
                "Document type metadata schema changed");
    }

    public void auditArchived(UUID actorId, DocumentType after) {
        auditEventService.record(
                AuditEventType.DOCUMENT_TYPE_ARCHIVED_AUDIT,
                actorId,
                "USER",
                AGGREGATE_TYPE,
                after.id(),
                after.organizationId(),
                after.workspaceId(),
                null,
                payload(after),
                "Document type archived");
    }

    public boolean isValidJson(String json) {
        if (json == null || json.isBlank()) {
            return true;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> payload(DocumentType dt) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", dt.id());
        map.put("code", dt.code().value());
        map.put("name", dt.name());
        map.put("scope", dt.documentScope().name());
        map.put("status", dt.status().name());
        map.put("organizationId", dt.organizationId());
        map.put("workspaceId", dt.workspaceId());
        map.put("defaultClassification",
                dt.defaultClassification() != null ? dt.defaultClassification().name() : null);
        map.put("builtIn", dt.builtIn());
        return map;
    }
}
