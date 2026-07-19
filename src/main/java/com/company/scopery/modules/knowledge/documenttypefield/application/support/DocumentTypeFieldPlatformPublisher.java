package com.company.scopery.modules.knowledge.documenttypefield.application.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.knowledge.documenttypefield.domain.model.DocumentTypeField;
import com.company.scopery.modules.knowledge.shared.listeners.KnowledgeEventDefinitionSeedInitializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DocumentTypeFieldPlatformPublisher {

    public static final String AGGREGATE_TYPE = "DOCUMENT_TYPE_FIELD";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;
    private final ObjectMapper objectMapper;

    public DocumentTypeFieldPlatformPublisher(TransactionalOutboxService outboxService,
                                              ImmutableAuditEventService auditEventService,
                                              ObjectMapper objectMapper) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
        this.objectMapper = objectMapper;
    }

    public void enqueue(DocumentTypeField field, String eventCode) {
        outboxService.enqueue(AGGREGATE_TYPE, field.id(), eventCode,
                KnowledgeEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, payload(field));
    }

    public void enqueueReorder(UUID documentTypeId, Object payload) {
        outboxService.enqueue("DOCUMENT_TYPE", documentTypeId, "DOCUMENT_TYPE_FIELDS_REORDERED",
                KnowledgeEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1, payload);
    }

    public void auditSchemaChange(UUID actorId, DocumentTypeField before, DocumentTypeField after, String reason) {
        auditEventService.record(
                AuditEventType.DOCUMENT_TYPE_FIELD_SCHEMA_CHANGED,
                actorId,
                "USER",
                AGGREGATE_TYPE,
                after.id(),
                null,
                null,
                Map.of("dataType", before.dataType().name(), "required", before.required()),
                Map.of("dataType", after.dataType().name(), "required", after.required()),
                reason);
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

    private Map<String, Object> payload(DocumentTypeField field) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", field.id());
        map.put("documentTypeId", field.documentTypeId());
        map.put("fieldKey", field.fieldKey().value());
        map.put("dataType", field.dataType().name());
        map.put("status", field.status().name());
        map.put("displayOrder", field.displayOrder());
        return map;
    }
}
